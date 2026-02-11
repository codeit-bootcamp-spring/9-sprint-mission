const API_BASE_URL = '/api';
const CHANNELS_ENDPOINT = `${API_BASE_URL}/channel/All`;
const USERS_ENDPOINT = `${API_BASE_URL}/user/findAll`;

// [절대 안 깨지는 주소] GitHub 호스팅 이미지 사용
const DEFAULT_ICON = 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png';
const CHANNEL_ICONS = [
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/badge-boulder.png',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/badge-cascade.png',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/badge-thunder.png',
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/badge-rainbow.png'
];

const POKEMONS = [
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png', // 피카츄
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png',  // 파이리
    'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png'   // 꼬부기
];

document.addEventListener('DOMContentLoaded', fetchAndRenderChannels);

async function fetchAndRenderChannels() {
    try {
        const tempUserId = '00000000-0000-0000-0000-000000000000';
        const response = await fetch(`${CHANNELS_ENDPOINT}?userId=${tempUserId}`);
        if (!response.ok) throw new Error('API 호출 실패');

        const channels = await response.json();
        const listEl = document.getElementById('channelList');

        listEl.innerHTML = channels.map((channel, i) => {
            const icon = CHANNEL_ICONS[i % CHANNEL_ICONS.length];
            const pIds = JSON.stringify(channel.participantIds || []);

            return `
                <div class="user-item" onclick='showUsers(${pIds}, "${channel.name}")'>
                    <img src="${icon}" class="user-avatar"
                         onerror="this.onerror=null; this.src='${DEFAULT_ICON}';">
                    <div class="user-info">
                        <div class="user-name">AREA: ${channel.name}</div>
                        <div class="user-email">${channel.description || '신비한 구역'}</div>
                    </div>
                    <div class="status-badge ${channel.type === 'PUBLIC' ? 'online' : 'offline'}">
                        ${channel.type}
                    </div>
                </div>
            `;
        }).join('');
    } catch (e) {
        console.error(e);
    }
}

async function showUsers(participantIds, channelName) {
    const modal = document.getElementById('userModal');
    const container = document.getElementById('channelUserList');
    document.getElementById('modalTitle').innerText = `${channelName}의 트레이너`;

    modal.style.display = 'flex';
    container.innerHTML = '<p style="text-align:center;">탐색 중...</p>';

    try {
        const res = await fetch(USERS_ENDPOINT);
        const allUsers = await res.json();

        const filtered = (participantIds && participantIds.length > 0)
            ? allUsers.filter(u => participantIds.includes(u.id))
            : allUsers;

        container.innerHTML = filtered.map((user, i) => `
            <div class="user-item" style="cursor: default; background: rgba(255,255,255,0.03);">
                <img src="${POKEMONS[i % POKEMONS.length]}" style="width:45px;" onerror="this.src='${DEFAULT_ICON}'">
                <div class="user-info">
                    <div class="user-name">${user.username}</div>
                </div>
                <div class="status-badge ${user.online ? 'online' : 'offline'}">
                    ${user.online ? 'READY' : 'AWAY'}
                </div>
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = '<p>데이터 로드 실패</p>';
    }
}

function closeModal() {
    document.getElementById('userModal').style.display = 'none';
}