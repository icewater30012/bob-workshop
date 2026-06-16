// 回饋查詢頁面 JavaScript

// API 基礎 URL
const API_BASE_URL = '/api';

// 全域變數
let allFeedbacks = [];
let allStations = [];
let feedbackModal;

// 頁面載入完成後初始化
document.addEventListener('DOMContentLoaded', function() {
    // 初始化 Bootstrap Modal
    feedbackModal = new bootstrap.Modal(document.getElementById('feedbackModal'));
    
    // 載入深色模式設定
    loadDarkModePreference();
    
    // 載入資料
    loadStations();
    loadFeedbacks();
    
    // 綁定事件監聽器
    bindEventListeners();
});

// 綁定事件監聽器
function bindEventListeners() {
    // 深色模式切換
    document.getElementById('darkModeToggle').addEventListener('click', toggleDarkMode);
    
    // 篩選按鈕
    document.getElementById('applyFilter').addEventListener('click', applyFilters);
    
    // 懸浮按鈕
    document.getElementById('floatingFeedbackBtn').addEventListener('click', function() {
        openFeedbackModal();
    });
    
    // 回饋表單提交
    document.getElementById('feedbackForm').addEventListener('submit', submitFeedback);
    
    // 星星評分
    const stars = document.querySelectorAll('#ratingStars i');
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.getAttribute('data-rating'));
            setRating(rating);
        });
        
        star.addEventListener('mouseenter', function() {
            const rating = parseInt(this.getAttribute('data-rating'));
            highlightStars(rating);
        });
    });
    
    document.getElementById('ratingStars').addEventListener('mouseleave', function() {
        const currentRating = parseInt(document.getElementById('feedbackRating').value) || 0;
        highlightStars(currentRating);
    });
}

// 載入深色模式偏好設定
function loadDarkModePreference() {
    const darkMode = localStorage.getItem('darkMode') === 'true';
    if (darkMode) {
        document.body.classList.add('dark-mode');
    }
}

// 切換深色模式
function toggleDarkMode() {
    document.body.classList.toggle('dark-mode');
    const isDarkMode = document.body.classList.contains('dark-mode');
    localStorage.setItem('darkMode', isDarkMode);
}

// 載入所有車站
async function loadStations() {
    try {
        const response = await fetch(`${API_BASE_URL}/stations`);
        if (!response.ok) throw new Error('無法載入車站資料');
        
        allStations = await response.json();
        populateStationSelects();
    } catch (error) {
        console.error('載入車站失敗:', error);
        showAlert('無法載入車站資料', 'danger');
    }
}

// 填充車站選單
function populateStationSelects() {
    const filterSelect = document.getElementById('filterStation');
    const feedbackSelect = document.getElementById('feedbackStation');
    
    // 清空現有選項
    filterSelect.innerHTML = '<option value="">所有車站</option>';
    feedbackSelect.innerHTML = '<option value="">請選擇車站</option>';
    
    // 加入車站選項
    allStations.forEach(station => {
        const filterOption = document.createElement('option');
        filterOption.value = station.id;
        filterOption.textContent = `${station.name} (${station.line})`;
        filterSelect.appendChild(filterOption);
        
        const feedbackOption = filterOption.cloneNode(true);
        feedbackSelect.appendChild(feedbackOption);
    });
}

// 載入所有回饋
async function loadFeedbacks() {
    try {
        const response = await fetch(`${API_BASE_URL}/feedbacks`);
        if (!response.ok) throw new Error('無法載入回饋資料');
        
        allFeedbacks = await response.json();
        displayFeedbacks(allFeedbacks);
        updateStatistics();
    } catch (error) {
        console.error('載入回饋失敗:', error);
        document.getElementById('feedbackList').innerHTML = 
            '<div class="alert alert-danger">無法載入回饋資料</div>';
    }
}

// 顯示回饋列表
function displayFeedbacks(feedbacks) {
    const container = document.getElementById('feedbackList');
    const countBadge = document.getElementById('feedbackCount');
    
    countBadge.textContent = `${feedbacks.length} 筆回饋`;
    
    if (feedbacks.length === 0) {
        container.innerHTML = '<div class="alert alert-info">目前沒有回饋資料</div>';
        return;
    }
    
    container.innerHTML = feedbacks.map(feedback => `
        <div class="feedback-list-item">
            <div class="feedback-header">
                <div>
                    <span class="feedback-station">${feedback.station.name}</span>
                    <span class="badge bg-secondary ms-2">${feedback.station.line}</span>
                </div>
                <div class="feedback-rating">
                    ${generateStarRating(feedback.rating)}
                </div>
            </div>
            <div class="feedback-comment">${escapeHtml(feedback.comment)}</div>
            <div class="feedback-meta">
                <span>
                    <i class="bi bi-person-fill"></i> 
                    ${feedback.passengerName || '匿名乘客'}
                </span>
                <span>
                    <i class="bi bi-calendar-fill"></i> 
                    ${formatDateTime(feedback.createdAt)}
                </span>
            </div>
        </div>
    `).join('');
}

// 生成星星評分顯示
function generateStarRating(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
            stars += '<i class="bi bi-star-fill" style="color: #ffc107;"></i>';
        } else {
            stars += '<i class="bi bi-star" style="color: #ddd;"></i>';
        }
    }
    return stars;
}

// 更新統計資訊
function updateStatistics() {
    const statsContainer = document.getElementById('feedbackStats');
    
    if (allFeedbacks.length === 0) {
        statsContainer.innerHTML = '<div class="col-12 text-center text-muted">尚無回饋資料</div>';
        return;
    }
    
    // 計算統計數據
    const totalFeedbacks = allFeedbacks.length;
    const avgRating = (allFeedbacks.reduce((sum, f) => sum + f.rating, 0) / totalFeedbacks).toFixed(1);
    
    // 計算各評分數量
    const ratingCounts = [0, 0, 0, 0, 0];
    allFeedbacks.forEach(f => ratingCounts[f.rating - 1]++);
    
    // 找出最受歡迎的車站
    const stationCounts = {};
    allFeedbacks.forEach(f => {
        const stationName = f.station.name;
        stationCounts[stationName] = (stationCounts[stationName] || 0) + 1;
    });
    const topStation = Object.entries(stationCounts).sort((a, b) => b[1] - a[1])[0];
    
    statsContainer.innerHTML = `
        <div class="col-md-4">
            <div class="stat-item">
                <div class="stat-value">${totalFeedbacks}</div>
                <div class="stat-label">總回饋數</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="stat-item">
                <div class="stat-value">${avgRating} ⭐</div>
                <div class="stat-label">平均評分</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="stat-item">
                <div class="stat-value">${topStation ? topStation[0] : 'N/A'}</div>
                <div class="stat-label">最多回饋車站</div>
            </div>
        </div>
        <div class="col-12 mt-3">
            <h6 class="mb-3">評分分布</h6>
            ${[5, 4, 3, 2, 1].map(rating => {
                const count = ratingCounts[rating - 1];
                const percentage = totalFeedbacks > 0 ? (count / totalFeedbacks * 100) : 0;
                return `
                    <div class="rating-bar">
                        <div class="rating-bar-label">${rating} ⭐</div>
                        <div class="rating-bar-fill">
                            <div class="rating-bar-value" style="width: ${percentage}%"></div>
                        </div>
                        <div class="rating-bar-count">${count}</div>
                    </div>
                `;
            }).join('')}
        </div>
    `;
}

// 套用篩選
function applyFilters() {
    const stationId = document.getElementById('filterStation').value;
    const rating = document.getElementById('filterRating').value;
    
    let filtered = allFeedbacks;
    
    if (stationId) {
        filtered = filtered.filter(f => f.station.id == stationId);
    }
    
    if (rating) {
        filtered = filtered.filter(f => f.rating == rating);
    }
    
    displayFeedbacks(filtered);
}

// 開啟回饋彈出視窗
function openFeedbackModal() {
    // 重置表單
    document.getElementById('feedbackForm').reset();
    document.getElementById('feedbackRating').value = '';
    highlightStars(0);
    
    feedbackModal.show();
}

// 設定評分
function setRating(rating) {
    document.getElementById('feedbackRating').value = rating;
    highlightStars(rating);
}

// 高亮星星
function highlightStars(rating) {
    const stars = document.querySelectorAll('#ratingStars i');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.remove('bi-star');
            star.classList.add('bi-star-fill', 'active');
        } else {
            star.classList.remove('bi-star-fill', 'active');
            star.classList.add('bi-star');
        }
    });
}

// 提交回饋
async function submitFeedback(event) {
    event.preventDefault();
    
    const stationId = document.getElementById('feedbackStation').value;
    const rating = document.getElementById('feedbackRating').value;
    const comment = document.getElementById('feedbackComment').value;
    const passengerName = document.getElementById('passengerName').value;
    
    if (!stationId || !rating) {
        showAlert('請選擇車站並給予評分', 'warning');
        return;
    }
    
    const feedbackData = {
        station: { id: parseInt(stationId) },
        rating: parseInt(rating),
        comment: comment.trim(),
        passengerName: passengerName.trim() || null
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/feedbacks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(feedbackData)
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || '提交失敗');
        }
        
        showAlert('回饋提交成功!感謝您的寶貴意見', 'success');
        feedbackModal.hide();
        
        // 重新載入回饋列表
        await loadFeedbacks();
        
    } catch (error) {
        console.error('提交回饋失敗:', error);
        showAlert('提交失敗: ' + error.message, 'danger');
    }
}

// 顯示提示訊息
function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
    alertDiv.style.zIndex = '9999';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alertDiv);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// 格式化日期時間
function formatDateTime(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// HTML 轉義
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Made with Bob
