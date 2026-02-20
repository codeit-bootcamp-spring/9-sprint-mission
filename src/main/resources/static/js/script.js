// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/users/findAll`,
    FILE: `${API_BASE_URL}/files`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('Failed to fetch users');

        const users = await response.json();
        console.log("받은 사용자 목록:", users);

        if (!Array.isArray(users)) {
            console.error("응답이 배열이 아님:", users);
            return;
        }

        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// Render user list
function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const profileImageUrl = user.profileId
            ? `${ENDPOINTS.FILE}/${user.profileId}`
            : '/default-avatar.png';

        userElement.innerHTML = `
            <img src="${profileImageUrl}" 
                 alt="${user.username ?? ''}" 
                 class="user-avatar"
                 onerror="this.src='/default-avatar.png'">
            <div class="user-info">
                <div class="user-name">${user.username ?? ''}</div>
                <div class="user-email">${user.email ?? ''}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}
