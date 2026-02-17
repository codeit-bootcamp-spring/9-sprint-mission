const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

const POKEMONS = [
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/25.gif',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/7.gif',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/4.gif',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/1.gif'
];

document.addEventListener('DOMContentLoaded', fetchAndRenderUsers);

async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('서버 연결 실패');
        const users = await response.json();

        // 데이터가 없는 경우 처리
        if (!users || users.length === 0) {
            document.getElementById('userList').innerHTML = '<p style="color:white; text-align:center;">등록된 트레이너가 없습니다.</p>';
            return;
        }

        renderUserList(users);
    } catch (error) {
        console.error('CRITICAL ERROR:', error);
        document.getElementById('userList').innerHTML = `<p style="color:white; text-align:center;">데이터 로드 실패: ${error.message}</p>`;
    }
}

// 이미지 처리를 비동기로 분리하여 리스트 렌더링 속도 최적화
function renderUserList(users) {
    const userListElement = document.getElementById('userList');

    // 일단 껍데기(리스트)부터 즉시 렌더링
    const html = users.map((user, index) => {
        const fallbackImg = POKEMONS[index % POKEMONS.length];
        return `
            <div class="user-item">
                <img src="${fallbackImg}" id="img-${user.id || index}" class="user-avatar" alt="avatar">
                <div class="user-info">
                    <div class="user-name">${user.username || 'Unknown Trainer'}</div>
                    <div class="user-email">${user.email || 'No Email'}</div>
                </div>
                <div class="status-badge ${user.online ? 'online' : 'offline'}">
                    ${user.online ? 'BATTLE READY' : 'RESTING'}
                </div>
            </div>
        `;
    }).join('');

    userListElement.innerHTML = html;

    // 리스트를 먼저 띄운 후, 프로필 이미지를 천천히 덮어씌움 (Lazy Load 방식)
    users.forEach(async (user, index) => {
        if (user.profileId) {
            try {
                const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${user.profileId}`);
                if (response.ok) {
                    const profile = await response.json();
                    const imgElement = document.getElementById(`img-${user.id || index}`);
                    if (imgElement) {
                        imgElement.src = `data:${profile.contentType};base64,${profile.bytes}`;
                    }
                }
            } catch (e) {
                console.warn("프로필 이미지 로드 실패, 포켓몬 유지");
            }
        }
    });
}