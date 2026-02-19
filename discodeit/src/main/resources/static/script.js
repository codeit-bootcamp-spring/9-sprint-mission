const API_BASE_URL = `${window.location.origin}/api`;
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT_DOWNLOAD_BASE: `${window.location.origin}/binary`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    void fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) {
            console.error('Failed to fetch users', response.status);
            return;
        }
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

function getUserProfileUrl(profileId) {
    if (!profileId) return '/default-avatar.png';
    return `${ENDPOINTS.BINARY_CONTENT_DOWNLOAD_BASE}/${profileId}/download`;
}

function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const img = document.createElement('img');
        img.className = 'user-avatar';
        img.alt = user.username;
        img.src = getUserProfileUrl(user.profileId);
        img.onerror = () => {
            img.onerror = null;
            img.src = '/default-avatar.png';
        };

        const info = document.createElement('div');
        info.className = 'user-info';

        const name = document.createElement('div');
        name.className = 'user-name';
        name.textContent = user.username;

        const email = document.createElement('div');
        email.className = 'user-email';
        email.textContent = user.email;

        info.appendChild(name);
        info.appendChild(email);

        const status = document.createElement('div');
        status.className = `status-badge ${user.online ? 'online' : 'offline'}`;
        status.textContent = user.online ? '온라인' : '오프라인';

        userElement.appendChild(img);
        userElement.appendChild(info);
        userElement.appendChild(status);

        userListElement.appendChild(userElement);
    }
}