/**
 * IBM Bob 知識測驗系統 - 重新設計版本
 * 優化效能、無障礙性、錯誤處理
 */

// 常數定義
const TOTAL_QUESTIONS = 50;
const STORAGE_KEY = 'bob-quiz-stats';
const RETRY_ATTEMPTS = 3;
const RETRY_DELAY = 1000;

// 全域變數
let questions = [];
let currentQuestion = null;
let answeredQuestions = new Set();
let stats = {
    correct: 0,
    wrong: 0,
    total: 0
};

// 分類對應的 CSS 類別
const categoryClasses = {
    '核心概念': 'badge-concept',
    '多模式能力': 'badge-mode',
    '企業功能': 'badge-enterprise',
    'SDLC 整合': 'badge-sdlc',
    '應用場景': 'badge-scenario',
    '技術細節': 'badge-technical'
};

// DOM 元素快取
const elements = {};

/**
 * 初始化 DOM 元素快取
 */
function cacheElements() {
    elements.category = document.getElementById('category');
    elements.questionText = document.getElementById('questionText');
    elements.optionsSection = document.getElementById('optionsSection');
    elements.explanationSection = document.getElementById('explanationSection');
    elements.explanationContent = document.getElementById('explanationContent');
    elements.progress = document.getElementById('progress');
    elements.correctCount = document.getElementById('correctCount');
    elements.wrongCount = document.getElementById('wrongCount');
    elements.accuracy = document.getElementById('accuracy');
    elements.nextBtn = document.getElementById('nextBtn');
    elements.resetBtn = document.getElementById('resetBtn');
    elements.loadingIndicator = document.getElementById('loadingIndicator');
    elements.errorAlert = document.getElementById('errorAlert');
    elements.errorMessage = document.getElementById('errorMessage');
}

/**
 * 顯示載入指示器
 */
function showLoading(show = true) {
    if (elements.loadingIndicator) {
        elements.loadingIndicator.style.display = show ? 'flex' : 'none';
    }
}

/**
 * 顯示錯誤訊息
 */
function showError(message) {
    if (elements.errorAlert && elements.errorMessage) {
        elements.errorMessage.textContent = message;
        elements.errorAlert.style.display = 'block';
        
        // 3 秒後自動關閉
        setTimeout(() => {
            hideError();
        }, 5000);
    }
    
    // 同時更新問題文字區域
    if (elements.questionText) {
        elements.questionText.textContent = `錯誤：${message}`;
        elements.questionText.setAttribute('role', 'alert');
    }
}

/**
 * 隱藏錯誤訊息
 */
function hideError() {
    if (elements.errorAlert) {
        elements.errorAlert.style.display = 'none';
    }
}

/**
 * 使用重試機制載入資料
 */
async function fetchWithRetry(url, attempts = RETRY_ATTEMPTS) {
    for (let i = 0; i < attempts; i++) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return await response.json();
        } catch (error) {
            if (i === attempts - 1) throw error;
            await new Promise(resolve => setTimeout(resolve, RETRY_DELAY * (i + 1)));
        }
    }
}

/**
 * 初始化測驗系統
 */
async function initQuiz() {
    try {
        showLoading(true);
        
        // 載入題庫
        const data = await fetchWithRetry('questions.json');
        questions = data.questions;
        
        if (!questions || questions.length === 0) {
            throw new Error('題庫為空');
        }
        
        // 載入儲存的統計資料
        loadStats();
        
        // 載入第一題
        loadRandomQuestion();
        
        showLoading(false);
    } catch (error) {
        showLoading(false);
        showError(`無法載入題庫：${error.message}。請檢查網路連線後重新整理頁面。`);
    }
}

/**
 * 載入統計資料
 */
function loadStats() {
    try {
        const saved = localStorage.getItem(STORAGE_KEY);
        if (saved) {
            const data = JSON.parse(saved);
            stats = { ...stats, ...data };
            updateStats();
        }
    } catch (error) {
        console.warn('無法載入統計資料:', error);
    }
}

/**
 * 儲存統計資料
 */
function saveStats() {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(stats));
    } catch (error) {
        console.warn('無法儲存統計資料:', error);
    }
}

/**
 * 載入隨機題目
 */
function loadRandomQuestion() {
    if (questions.length === 0) {
        showError('題庫尚未載入');
        return;
    }
    
    // 隨機選擇題目
    const randomIndex = Math.floor(Math.random() * questions.length);
    currentQuestion = questions[randomIndex];
    
    // 記錄已答題目（限制大小避免記憶體洩漏）
    answeredQuestions.add(currentQuestion.id);
    if (answeredQuestions.size > TOTAL_QUESTIONS * 2) {
        // 保留最近的題目
        const recent = Array.from(answeredQuestions).slice(-TOTAL_QUESTIONS);
        answeredQuestions = new Set(recent);
    }
    
    // 顯示題目
    displayQuestion(currentQuestion);
    
    // 更新進度
    updateProgress();
    
    // 重置按鈕狀態
    if (elements.nextBtn) {
        elements.nextBtn.disabled = false;
    }
}

/**
 * 顯示題目
 */
function displayQuestion(question) {
    // 更新分類標籤
    const categoryClass = categoryClasses[question.category] || 'badge-concept';
    elements.category.className = `category-badge ${categoryClass}`;
    elements.category.textContent = question.category;
    
    // 更新題目文字
    elements.questionText.textContent = question.question;
    elements.questionText.removeAttribute('role'); // 移除 alert role
    
    // 生成選項
    elements.optionsSection.innerHTML = '';
    
    question.options.forEach((option, index) => {
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'option';
        button.setAttribute('role', 'radio');
        button.setAttribute('aria-checked', 'false');
        button.setAttribute('tabindex', '0');
        button.setAttribute('aria-label', `選項 ${String.fromCharCode(65 + index)}: ${option}`);
        
        const label = document.createElement('span');
        label.className = 'option-label';
        label.textContent = String.fromCharCode(65 + index);
        label.setAttribute('aria-hidden', 'true');
        
        const text = document.createElement('span');
        text.className = 'option-text';
        text.textContent = option;
        
        button.appendChild(label);
        button.appendChild(text);
        
        // 使用事件委派
        button.addEventListener('click', () => checkAnswer(index));
        button.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                checkAnswer(index);
            }
        });
        
        elements.optionsSection.appendChild(button);
    });
    
    // 隱藏解釋區
    elements.explanationSection.style.display = 'none';
    elements.explanationSection.setAttribute('aria-hidden', 'true');
}

/**
 * 檢查答案
 */
function checkAnswer(selectedIndex) {
    if (!currentQuestion) return;
    
    const options = elements.optionsSection.querySelectorAll('.option');
    const correctIndex = currentQuestion.correct;
    
    // 禁用所有選項
    options.forEach((option, index) => {
        option.classList.add('disabled');
        option.disabled = true;
        option.setAttribute('aria-disabled', 'true');
        
        // 更新 aria-checked
        if (index === correctIndex) {
            option.setAttribute('aria-checked', 'true');
        }
    });
    
    // 標記正確答案
    options[correctIndex].classList.add('correct');
    
    // 檢查是否答對
    const isCorrect = selectedIndex === correctIndex;
    
    if (isCorrect) {
        stats.correct++;
        stats.total++;
    } else {
        options[selectedIndex].classList.add('wrong');
        stats.wrong++;
        stats.total++;
    }
    
    // 更新統計並儲存
    updateStats();
    saveStats();
    
    // 顯示解釋
    showExplanation(currentQuestion.explanation, isCorrect);
}

/**
 * 顯示解釋
 */
function showExplanation(explanation, isCorrect) {
    const icon = isCorrect ? '✅' : '❌';
    const status = isCorrect ? '答對了！' : '答錯了！';
    
    elements.explanationContent.innerHTML = `
        <div style="margin-bottom: var(--space-2);">
            <strong><span aria-hidden="true">${icon}</span> ${status}</strong>
        </div>
        <div>${explanation}</div>
    `;
    
    elements.explanationSection.style.display = 'block';
    elements.explanationSection.removeAttribute('aria-hidden');
    
    // 將焦點移至解釋區以便螢幕閱讀器朗讀
    elements.explanationSection.focus();
}

/**
 * 更新統計資訊（帶動畫）
 */
function updateStats() {
    // 更新數字並添加動畫效果（如果元素存在）
    updateStatValue(elements.correctCount, stats.correct);
    updateStatValue(elements.wrongCount, stats.wrong);
    
    // 計算正確率
    const accuracy = stats.total > 0
        ? Math.round((stats.correct / stats.total) * 100)
        : 0;
    updateStatValue(elements.accuracy, `${accuracy}%`);
}

/**
 * 更新單一統計值（帶動畫）
 */
function updateStatValue(element, value) {
    if (!element) return;
    
    element.classList.add('updating');
    element.textContent = value;
    
    setTimeout(() => {
        element.classList.remove('updating');
    }, 200);
}

/**
 * 更新進度
 */
function updateProgress() {
    if (!elements.progress) return;
    const current = Math.min(answeredQuestions.size, TOTAL_QUESTIONS);
    elements.progress.textContent = `${current} / ${TOTAL_QUESTIONS}`;
}

/**
 * 重新開始測驗
 */
function resetQuiz() {
    if (!confirm('確定要重新開始測驗嗎？這將清除所有答題記錄。')) {
        return;
    }
    
    // 重置統計
    stats = {
        correct: 0,
        wrong: 0,
        total: 0
    };
    answeredQuestions.clear();
    
    // 清除儲存的資料
    try {
        localStorage.removeItem(STORAGE_KEY);
    } catch (error) {
        console.warn('無法清除儲存資料:', error);
    }
    
    // 更新顯示（如果元素存在）
    updateStats();
    updateProgress();
    
    // 載入新題目
    loadRandomQuestion();
}

/**
 * 鍵盤快捷鍵支援
 */
function handleKeyboard(e) {
    // 數字鍵 1-4 選擇選項
    if (e.key >= '1' && e.key <= '4') {
        const index = parseInt(e.key) - 1;
        const options = elements.optionsSection.querySelectorAll('.option');
        if (options[index] && !options[index].disabled) {
            e.preventDefault();
            checkAnswer(index);
        }
    }
    
    // 空白鍵或 Enter 載入下一題
    if ((e.key === ' ' || e.key === 'Enter') && e.target === document.body) {
        e.preventDefault();
        if (elements.nextBtn && !elements.nextBtn.disabled) {
            loadRandomQuestion();
        }
    }
    
    // R 鍵重新開始（如果按鈕存在）
    if ((e.key === 'r' || e.key === 'R') && e.target === document.body && elements.resetBtn) {
        e.preventDefault();
        resetQuiz();
    }
    
    // Escape 關閉錯誤訊息
    if (e.key === 'Escape') {
        hideError();
    }
}

/**
 * 防止意外離開頁面
 */
function handleBeforeUnload(e) {
    if (stats.total > 0) {
        e.preventDefault();
        e.returnValue = '您的答題記錄將會遺失，確定要離開嗎？';
        return e.returnValue;
    }
}

/**
 * 清理事件監聽器
 */
function cleanup() {
    document.removeEventListener('keydown', handleKeyboard);
    window.removeEventListener('beforeunload', handleBeforeUnload);
}

/**
 * 頁面載入完成後初始化
 */
document.addEventListener('DOMContentLoaded', () => {
    // 快取 DOM 元素
    cacheElements();
    
    // 初始化測驗
    initQuiz();
    
    // 綁定事件
    if (elements.nextBtn) {
        elements.nextBtn.addEventListener('click', loadRandomQuestion);
    }
    
    if (elements.resetBtn) {
        elements.resetBtn.addEventListener('click', resetQuiz);
    }
    
    // 錯誤訊息關閉按鈕
    const errorClose = elements.errorAlert?.querySelector('.error-close');
    if (errorClose) {
        errorClose.addEventListener('click', hideError);
    }
    
    // 鍵盤事件
    document.addEventListener('keydown', handleKeyboard);
    
    // 防止意外離開
    window.addEventListener('beforeunload', handleBeforeUnload);
    
    // 頁面卸載時清理
    window.addEventListener('unload', cleanup);
});

/**
 * 視窗大小改變時的處理（使用防抖）
 */
let resizeTimeout;
window.addEventListener('resize', () => {
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
        // 可以在這裡添加響應式調整邏輯
    }, 250);
});

/**
 * 頁面可見性變化處理
 */
document.addEventListener('visibilitychange', () => {
    if (document.hidden) {
        // 頁面隱藏時儲存狀態
        saveStats();
    }
});

// Made with Bob
