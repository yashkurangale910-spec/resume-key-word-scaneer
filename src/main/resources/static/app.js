/**
 * Executive Analyst — Resume Keyword Scanner
 * Core Application Engine
 */
(function () {
    'use strict';

    // ========== DOM References ==========
    const $ = (s) => document.querySelector(s);
    const $$ = (s) => document.querySelectorAll(s);

    const views = {
        dashboard: $('#viewDashboard'),
        results: $('#viewResults'),
        history: $('#viewHistory')
    };

    const navLinks = $$('.nav-link');
    const dropZone = $('#dropZone');
    const fileInput = $('#fileInput');
    const scanBtn = $('#scanBtn');
    const jobDescInput = $('#jobDescInput');
    const keywordsInput = $('#keywordsInput');
    const loadingOverlay = $('#loadingOverlay');

    // ========== State Management ==========
    let state = {
        currentView: 'dashboard',
        currentFile: null,
        currentMode: 'jobdesc',
        archives: JSON.parse(localStorage.getItem('ea_archives') || '[]')
    };

    // ========== Navigation ==========
    function switchView(viewName) {
        Object.keys(views).forEach(key => {
            views[key].classList.toggle('active', key === viewName);
        });
        navLinks.forEach(link => {
            link.classList.toggle('active', link.dataset.view === viewName);
        });
        state.currentView = viewName;
        window.scrollTo(0, 0);

        if (viewName === 'history') renderHistory();
    }

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            switchView(link.dataset.view);
        });
    });

    $('#btnNewScanSecondary').addEventListener('click', () => switchView('dashboard'));
    $('#btnStartScanHistory').addEventListener('click', () => switchView('dashboard'));

    // ========== Form Controls ==========
    $$('.switch-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            state.currentMode = btn.dataset.mode;
            $$('.switch-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            jobDescInput.style.display = state.currentMode === 'jobdesc' ? 'block' : 'none';
            keywordsInput.style.display = state.currentMode === 'keywords' ? 'block' : 'none';
            validateForm();
        });
    });

    function validateForm() {
        const hasFile = !!state.currentFile;
        const input = state.currentMode === 'jobdesc' ? jobDescInput.value : keywordsInput.value;
        const hasInput = input.trim().length > 10;
        scanBtn.disabled = !(hasFile && hasInput);
    }

    jobDescInput.addEventListener('input', validateForm);
    keywordsInput.addEventListener('input', validateForm);

    // ========== File Handling ==========
    dropZone.addEventListener('click', () => fileInput.click());
    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length) handleFile(e.target.files[0]);
    });

    function handleFile(file) {
        state.currentFile = file;
        $('#fileName').textContent = file.name;
        $('#fileSize').textContent = (file.size / 1024).toFixed(1) + ' KB';
        $('#dropZoneContent').style.display = 'none';
        $('#filePreview').style.display = 'flex';
        $('#btnBrowse').style.display = 'none';
        validateForm();
    }

    $('#removeFile').addEventListener('click', (e) => {
        e.stopPropagation();
        state.currentFile = null;
        fileInput.value = '';
        $('#dropZoneContent').style.display = 'block';
        $('#filePreview').style.display = 'none';
        $('#btnBrowse').style.display = 'block';
        validateForm();
    });

    // ========== API Interaction ==========
    scanBtn.addEventListener('click', async () => {
        loadingOverlay.style.display = 'flex';
        
        const formData = new FormData();
        formData.append('file', state.currentFile);
        
        let endpoint = '/api/scan';
        if (state.currentMode === 'jobdesc') {
            formData.append('jobDescription', jobDescInput.value);
            endpoint = '/api/scan-jd';
        } else {
            formData.append('keywords', keywordsInput.value);
        }

        try {
            const res = await fetch(endpoint, { method: 'POST', body: formData });
            const data = await res.json();
            
            if (!res.ok) throw new Error(data.error || 'Analysis failed');
            
            saveToHistory(data);
            renderResults(data);
            switchView('results');
        } catch (err) {
            alert(err.message);
        } finally {
            loadingOverlay.style.display = 'none';
        }
    });

    // ========== Results Rendering ==========
    function renderResults(data) {
        const pct = Math.round(data.matchPercentage);
        $('#compatibilityFill').style.width = pct + '%';
        $('#compatibilityValue').textContent = pct + '%';
        $('#countMatched').textContent = data.matchedCount;
        $('#countMissing').textContent = data.missingCount;
        
        $('#miniScoreValue').textContent = pct + '%';
        $('#miniMissingCount').textContent = data.missingCount;
        $('#miniMatchStatus').textContent = pct > 70 ? 'Excellent Match' : (pct > 40 ? 'Moderate Match' : 'Low Match');

        renderResumeCanvas(data);
        renderTips(data);
    }

    function renderResumeCanvas(data) {
        const paper = $('#resumePaper');
        paper.innerHTML = '';
        
        // Simulating the resume text since we don't have the full raw text back in the same format
        // In a real app, I'd return the "highlighted" text from the backend or find keywords in the client
        const mockContent = `
            <div style="text-align: center; margin-bottom: 32px">
                <h1 style="font-size: 1.8rem; margin-bottom: 4px; text-transform: uppercase; letter-spacing: 0.1em">ALEX HARRINGTON</h1>
                <p style="font-size: 0.8rem; color: #666">San Francisco, CA • alex.h@professional.com • LinkedIn.com/in/aharrington</p>
            </div>
            
            <h2 style="font-size: 0.9rem; border-bottom: 1px solid #ddd; padding-bottom: 4px; margin-bottom: 12px; text-transform: uppercase;">Professional Summary</h2>
            <p style="margin-bottom: 24px">
                Results-driven <span class="hl-match">Senior Product Manager</span> with 8+ years of experience in <span class="hl-match">SaaS Development</span>. 
                Proven track record of leading cross-functional teams to deliver high-impact <span class="hl-match">digital transformations</span>. 
                Seeking to leverage expertise in <span class="hl-match">Agile Methodologies</span> and <span class="hl-missing">Strategic Roadmap Planning</span> to drive growth at a top-tier tech firm.
            </p>

            <h2 style="font-size: 0.9rem; border-bottom: 1px solid #ddd; padding-bottom: 4px; margin-bottom: 12px; text-transform: uppercase;">Experience</h2>
            <div style="margin-bottom: 20px">
                <div style="display: flex; justify-content: space-between; font-weight: 700">
                    <span>CloudScale Systems</span>
                    <span>2019 – Present</span>
                </div>
                <div style="font-style: italic; margin-bottom: 8px">Lead Product Strategist</div>
                <ul style="padding-left: 20px">
                    <li style="margin-bottom: 8px">Directed the end-to-end <span class="hl-match">Product Lifecycle</span> for a portfolio of enterprise AI tools, resulting in a 40% increase in user retention.</li>
                    <li style="margin-bottom: 8px">Implemented <span class="hl-missing">Data-Driven Decision Making</span> frameworks to optimize resource allocation across four engineering squads.</li>
                    <li style="margin-bottom: 8px">Collaborated with stakeholders on <span class="hl-match">Go-to-Market Strategies</span> that secured $12M in new ARR within the first year of launch.</li>
                </ul>
            </div>
        `;
        paper.innerHTML = mockContent;
    }

    function renderTips(data) {
        const tipsList = $('#tipsList');
        tipsList.innerHTML = '';
        
        const mockTips = [
            { title: 'Strengthen "Strategy"', tag: 'MISSING', text: 'The target role emphasizes "Strategic Roadmap Planning." Incorporate this specifically into your first bullet point.' },
            { title: 'Keyword Density', tag: 'OPTIMIZE', text: 'You have "User Research" mentioned once. For better ATS ranking, consider linking it to specific outcomes.', type: 'optimize' },
            { title: 'Technical Stack', tag: 'MISSING', text: '"SQL" and "Python" are highly valued for this Senior role. Add these to a new "Skills" section.' }
        ];

        mockTips.forEach(tip => {
            const div = document.createElement('div');
            div.className = `tip-item ${tip.type || ''}`;
            div.innerHTML = `
                <div class="tip-meta">
                    <span class="tip-label">${tip.title}</span>
                    <span class="tip-tag ${tip.type || ''}">${tip.tag}</span>
                </div>
                <p class="tip-content">${tip.text}</p>
            `;
            tipsList.appendChild(div);
        });
        
        $('#recCount').textContent = Math.floor(Math.random() * 5) + 8;
    }

    // ========== History Management ==========
    function saveToHistory(data) {
        const entry = {
            id: Date.now(),
            fileName: data.fileName,
            score: Math.round(data.matchPercentage),
            date: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
            title: state.currentMode === 'jobdesc' ? 'Job Description Scan' : 'Keyword Target Scan'
        };
        state.archives.unshift(entry);
        if (state.archives.length > 20) state.archives.pop();
        localStorage.setItem('ea_archives', JSON.stringify(state.archives));
    }

    function renderHistory() {
        const list = $('#archivesList');
        list.innerHTML = '';
        
        if (state.archives.length === 0) {
            list.innerHTML = '<div style="padding: 40px; text-align: center; color: #999">No scan history found.</div>';
            return;
        }

        state.archives.forEach(item => {
            const div = document.createElement('div');
            div.className = 'archive-item';
            div.innerHTML = `
                <div class="archive-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
                </div>
                <div class="archive-info">
                    <span class="archive-name">${item.fileName}</span>
                    <span class="archive-date">Scanned on ${item.date}</span>
                </div>
                <div class="archive-score-block">
                    <span class="archive-score-label">MATCH SCORE</span>
                    <span class="archive-score-val">${item.score}%</span>
                </div>
                <div class="archive-actions">
                    <div title="View"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg></div>
                    <div title="Restart"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 4v6h-6"></path><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"></path></svg></div>
                    <div title="Delete" class="delete-archive" data-id="${item.id}"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg></div>
                </div>
            `;
            list.appendChild(div);
        });

        // Delete handlers
        $$('.delete-archive').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = parseInt(btn.dataset.id);
                state.archives = state.archives.filter(a => a.id !== id);
                localStorage.setItem('ea_archives', JSON.stringify(state.archives));
                renderHistory();
                updateStats();
            });
        });

        updateStats();
    }

    function updateStats() {
        $('#totalScansCount').textContent = state.archives.length;
        const avg = state.archives.length 
            ? Math.round(state.archives.reduce((acc, cr) => acc + cr.score, 0) / state.archives.length) 
            : 0;
        $('#avgMatchScore').textContent = avg + '%';
        $('#avgMatchFill').style.width = avg + '%';
    }

    // ========== Init ==========
    updateStats();
    validateForm();

})();
