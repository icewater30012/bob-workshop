// API 基礎 URL
const API_URL = '/api/stations';
const FEEDBACK_API_URL = '/api/feedbacks';

// 全域變數
let allStations = [];
let feedbackModal;
let currentRating = 0;

// 頁面載入時執行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化 Bootstrap Modal
    feedbackModal = new bootstrap.Modal(document.getElementById('feedbackModal'));
    
    initDarkMode();
    loadStations();
    setupFormSubmit();
    setupFloatingButton();
    setupFeedbackModal();
});

/**
 * 初始化深色模式
 */
function initDarkMode() {
    const darkModeToggle = document.getElementById('darkModeToggle');
    const savedMode = localStorage.getItem('darkMode');
    
    // 載入儲存的模式
    if (savedMode === 'enabled') {
        document.body.classList.add('dark-mode');
    }
    
    // 切換深色模式
    darkModeToggle.addEventListener('click', function() {
        document.body.classList.toggle('dark-mode');
        
        if (document.body.classList.contains('dark-mode')) {
            localStorage.setItem('darkMode', 'enabled');
        } else {
            localStorage.setItem('darkMode', 'disabled');
        }
    });
}

/**
 * 載入所有車站
 */
async function loadStations() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error('載入車站失敗');
        }
        allStations = await response.json();
        displayStations(allStations);
        populateFeedbackStationSelect(allStations);
    } catch (error) {
        console.error('載入車站錯誤:', error);
        showError('載入車站資料失敗，請重新整理頁面');
    }
}

/**
 * 顯示車站列表
 */
function displayStations(stations) {
    const tbody = document.getElementById('stationList');
    
    if (stations.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center text-muted">
                    <i class="bi bi-inbox"></i> 目前沒有車站資料
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = stations.map(station => `
        <tr>
            <td><strong>${escapeHtml(station.code)}</strong></td>
            <td>${escapeHtml(station.name)}</td>
            <td>
                <span class="badge ${station.line === '紅線' ? 'bg-danger' : 'bg-warning text-dark'}">
                    ${escapeHtml(station.line)}
                </span>
            </td>
            <td>${formatDateTime(station.createdAt)}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-outline-danger" onclick="deleteStation(${station.id}, '${escapeHtml(station.name)}')">
                    <i class="bi bi-trash"></i> 刪除
                </button>
            </td>
        </tr>
    `).join('');
}

/**
 * 填充回饋表單的車站選擇下拉選單
 */
function populateFeedbackStationSelect(stations) {
    const feedbackStationSelect = document.getElementById('feedbackStation');
    
    // 清空現有選項
    feedbackStationSelect.innerHTML = '<option value="">請選擇車站</option>';
    
    // 加入車站選項
    stations.forEach(station => {
        const option = document.createElement('option');
        option.value = station.id;
        option.textContent = `${station.code} - ${station.name}`;
        feedbackStationSelect.appendChild(option);
    });
}

/**
 * 設定表單提交事件
 */
function setupFormSubmit() {
    const form = document.getElementById('stationForm');
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const station = {
            code: document.getElementById('code').value.trim(),
            name: document.getElementById('name').value.trim(),
            line: document.getElementById('line').value
        };
        
        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(station)
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || '新增車站失敗');
            }
            
            // 成功後重置表單並重新載入列表
            form.reset();
            showSuccess('車站新增成功！');
            loadStations();
        } catch (error) {
            console.error('新增車站錯誤:', error);
            showError(error.message || '新增車站失敗，請檢查輸入資料');
        }
    });
}

/**
 * 設定懸浮按鈕
 */
function setupFloatingButton() {
    const floatingBtn = document.getElementById('floatingFeedbackBtn');
    floatingBtn.addEventListener('click', function() {
        openFeedbackModal();
    });
}

/**
 * 設定回饋彈出視窗
 */
function setupFeedbackModal() {
    // 設定評分星星
    const stars = document.querySelectorAll('#ratingStars i');
    
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.getAttribute('data-rating'));
            currentRating = rating;
            document.getElementById('feedbackRating').value = rating;
            
            // 更新星星顯示
            stars.forEach((s, index) => {
                if (index < rating) {
                    s.classList.add('active');
                    s.classList.remove('bi-star');
                    s.classList.add('bi-star-fill');
                } else {
                    s.classList.remove('active');
                    s.classList.remove('bi-star-fill');
                    s.classList.add('bi-star');
                }
            });
        });
        
        // 滑鼠懸停效果
        star.addEventListener('mouseenter', function() {
            const rating = parseInt(this.getAttribute('data-rating'));
            stars.forEach((s, index) => {
                if (index < rating) {
                    s.classList.add('bi-star-fill');
                    s.classList.remove('bi-star');
                } else {
                    s.classList.add('bi-star');
                    s.classList.remove('bi-star-fill');
                }
            });
        });
    });
    
    // 滑鼠離開時恢復當前評分
    document.getElementById('ratingStars').addEventListener('mouseleave', function() {
        stars.forEach((s, index) => {
            if (index < currentRating) {
                s.classList.add('bi-star-fill');
                s.classList.remove('bi-star');
            } else {
                s.classList.add('bi-star');
                s.classList.remove('bi-star-fill');
            }
        });
    });
    
    // 設定表單提交
    const form = document.getElementById('feedbackForm');
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const stationId = document.getElementById('feedbackStation').value;
        const rating = document.getElementById('feedbackRating').value;
        const comment = document.getElementById('feedbackComment').value.trim();
        const passengerName = document.getElementById('passengerName').value.trim();
        
        if (!stationId) {
            showError('請選擇車站');
            return;
        }
        
        if (!rating) {
            showError('請選擇評分');
            return;
        }
        
        const feedback = {
            station: { id: parseInt(stationId) },
            rating: parseInt(rating),
            comment: comment,
            passengerName: passengerName || null
        };
        
        try {
            const response = await fetch(FEEDBACK_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(feedback)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || '提交回饋失敗');
            }
            
            // 成功後重置表單
            form.reset();
            currentRating = 0;
            stars.forEach(s => {
                s.classList.remove('active', 'bi-star-fill');
                s.classList.add('bi-star');
            });
            
            showSuccess('回饋提交成功！感謝您的寶貴意見');
            feedbackModal.hide();
        } catch (error) {
            console.error('提交回饋錯誤:', error);
            showError(error.message || '提交回饋失敗，請稍後再試');
        }
    });
}

/**
 * 開啟回饋彈出視窗
 */
function openFeedbackModal() {
    // 重置表單
    document.getElementById('feedbackForm').reset();
    document.getElementById('feedbackRating').value = '';
    currentRating = 0;
    
    // 重置星星
    const stars = document.querySelectorAll('#ratingStars i');
    stars.forEach(s => {
        s.classList.remove('active', 'bi-star-fill');
        s.classList.add('bi-star');
    });
    
    feedbackModal.show();
}

/**
 * 刪除車站
 */
async function deleteStation(id, name) {
    if (!confirm(`確定要刪除車站「${name}」嗎？`)) {
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || '刪除車站失敗');
        }
        
        showSuccess('車站刪除成功！');
        loadStations();
    } catch (error) {
        console.error('刪除車站錯誤:', error);
        showError(error.message || '刪除車站失敗');
    }
}

/**
 * 格式化日期時間
 */
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

/**
 * 跳脫 HTML 特殊字元
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * 顯示成功訊息
 */
function showSuccess(message) {
    showAlert(message, 'success');
}

/**
 * 顯示錯誤訊息
 */
function showError(message) {
    showAlert(message, 'danger');
}

/**
 * 顯示提示訊息
 */
function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
    alertDiv.style.zIndex = '9999';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alertDiv);
    
    // 3 秒後自動移除
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

// Made with Bob
