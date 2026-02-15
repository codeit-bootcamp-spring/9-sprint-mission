const API_BASE_URL = 'http://localhost:8080/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/users`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContents`,
    UPDATE: `${API_BASE_URL}/users`,
    DELETE: `${API_BASE_URL}/users`
};

document.addEventListener('DOMContentLoaded', fetchAndRenderUsers);

async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('목록 로드 실패:', error);
    }
}

async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}/${profileId}`);
        const profile = await response.json();
        return `data:${profile.contentType};base64,${profile.bytes}`;
    } catch (error) {
        return 'https://via.placeholder.com/60';
    }
}

async function renderUserList(users) {
    const list = document.getElementById('userList');
    list.innerHTML = '';

    for (const user of users) {
        const id = user.userId || user.id;
        const profileUrl = user.profileId ? await fetchUserProfile(user.profileId) : 'https://via.placeholder.com/60';

        const item = document.createElement('div');
        item.className = 'user-item';
        
        // 배지 디자인을 위해 클래스(status-badge online/offline)를 다시 적용했습니다.
        item.innerHTML = `
            <img src="${profileUrl}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
            <div class="user-actions">
                <button class="btn-edit" onclick="openUpdateModal('${id}', '${user.username}', '${user.email}')">수정</button>
                <button class="btn-delete" onclick="deleteUser('${id}')">삭제</button>
            </div>
        `;
        list.appendChild(item);
    }
}

function openUpdateModal(id, name, email) {
    document.getElementById('updateUserId').value = id;
    document.getElementById('updateNewName').value = name;
    document.getElementById('updateNewEmail').value = email;
    document.getElementById('updateNewPassword').value = '';
    document.getElementById('updateModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('updateModal').style.display = 'none';
}

async function submitUpdate() {
    const requestData = {
        userId: document.getElementById('updateUserId').value,
        newName: document.getElementById('updateNewName').value,
        newEmail: document.getElementById('updateNewEmail').value,
        newPassword: document.getElementById('updateNewPassword').value
    };

    if(!requestData.newPassword) return alert("비밀번호를 입력해주세요.");

    const response = await fetch(ENDPOINTS.UPDATE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestData)
    });

    if (response.ok) {
        alert("수정 완료");
        closeModal();
        fetchAndRenderUsers();
    }
}

async function deleteUser(id) {
    if(!confirm("삭제할까요?")) return;
    const response = await fetch(`${ENDPOINTS.DELETE}?userId=${id}`, { method: 'DELETE' });
    if (response.ok) fetchAndRenderUsers();
}