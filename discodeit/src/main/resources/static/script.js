
const API = {
    USERS_FIND_ALL: "/users", // <- 너 프로젝트에서 전체조회 URL로 바꿔도 됨
    // 프로필 이미지가 있다면: "/binary-contents/{id}" 형태일 수도 있음
    // 지금은 이미지 없어도 되도록 안전처리해둠
};

const $ = (id) => document.getElementById(id);

const stateEl = $("state");
const gridEl = $("grid");
const qEl = $("q");
const sortEl = $("sort");
const reloadEl = $("reload");
const metaEl = $("meta");

let users = [];

function showState(msg) {
    stateEl.hidden = !msg;
    stateEl.textContent = msg || "";
}

function safeText(v) {
    return (v ?? "").toString();
}

function toDate(v) {
    if (!v) return null;
    const d = new Date(v);
    return isNaN(d.getTime()) ? null : d;
}

function formatDate(v) {
    const d = toDate(v);
    if (!d) return "-";
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mm = String(d.getMinutes()).padStart(2, "0");
    return `${y}-${m}-${day} ${hh}:${mm}`;
}

function normalizeUser(u) {
    // 다양한 DTO 필드명을 흡수(너가 뭘 보내도 최대한 보이게)
    return {
        id: u.id,
        username: u.username ?? u.name ?? u.userName ?? "이름없음",
        email: u.email ?? u.mail ?? "-",
        online: typeof u.online === "boolean" ? u.online : !!u.isOnline,
        createdAt: u.createdAt ?? u.created_at ?? null,
        updatedAt: u.updatedAt ?? u.updated_at ?? null,
        profileId: u.profileId ?? u.profile_id ?? null,
    };
}

function sortUsers(list, mode) {
    const copy = [...list];
    const byUsername = (a, b) => safeText(a.username).localeCompare(safeText(b.username), "ko");
    const byCreated = (a, b) => (toDate(a.createdAt)?.getTime() ?? 0) - (toDate(b.createdAt)?.getTime() ?? 0);

    switch (mode) {
        case "createdAt_asc": copy.sort(byCreated); break;
        case "createdAt_desc": copy.sort((a, b) => -byCreated(a, b)); break;
        case "username_desc": copy.sort((a, b) => -byUsername(a, b)); break;
        case "username_asc":
        default: copy.sort(byUsername); break;
    }
    return copy;
}

function filterUsers(list, q) {
    const s = safeText(q).trim().toLowerCase();
    if (!s) return list;
    return list.filter(u =>
        safeText(u.username).toLowerCase().includes(s) ||
        safeText(u.email).toLowerCase().includes(s)
    );
}

function avatarUrl(u) {
    // 프로필 이미지 없어도 렌더되게 기본 아바타(초기 글자) 처리
    // 만약 너가 binaryContent 다운로드 URL이 있으면 여기에서 조합하면 됨.
    return null;
}

function render(list) {
    gridEl.innerHTML = "";

    if (!list.length) {
        showState("표시할 사용자가 없습니다. (API 응답이 비었거나, 검색 조건에 걸렸을 수 있음)");
        metaEl.textContent = "0 users";
        return;
    }

    showState("");
    metaEl.textContent = `${list.length} users`;

    for (const u of list) {
        const card = document.createElement("article");
        card.className = "card";

        const row = document.createElement("div");
        row.className = "row";

        const img = document.createElement("img");
        img.className = "avatar";
        img.alt = "avatar";

        const url = avatarUrl(u);
        if (url) {
            img.src = url;
        } else {
            // 이미지 없으면 색 배경 + 이니셜 느낌(간단하게 처리)
            img.src =
                "data:image/svg+xml;utf8," +
                encodeURIComponent(`
          <svg xmlns="http://www.w3.org/2000/svg" width="80" height="80">
            <defs>
              <linearGradient id="g" x1="0" x2="1" y1="0" y2="1">
                <stop offset="0" stop-color="#3a66ff"/>
                <stop offset="1" stop-color="#35d07f"/>
              </linearGradient>
            </defs>
            <rect rx="18" ry="18" width="80" height="80" fill="url(#g)"/>
            <text x="50%" y="55%" text-anchor="middle" font-size="34" fill="white" font-family="Arial" font-weight="700">
              ${safeText(u.username).trim().slice(0,1) || "?"}
            </text>
          </svg>
        `);
        }

        const info = document.createElement("div");
        const h = document.createElement("h3");
        h.className = "name";
        h.textContent = safeText(u.username);

        const p = document.createElement("p");
        p.className = "email";
        p.title = safeText(u.email);
        p.textContent = safeText(u.email);

        info.appendChild(h);
        info.appendChild(p);

        const badge = document.createElement("span");
        badge.className = "badge " + (u.online ? "on" : "off");
        badge.textContent = u.online ? "온라인" : "오프라인";

        row.appendChild(img);
        row.appendChild(info);
        row.appendChild(badge);

        const meta = document.createElement("div");
        meta.className = "meta";
        meta.innerHTML = `
      <span>가입: ${formatDate(u.createdAt)}</span>
      <span>ID: ${safeText(u.id).slice(0,8)}…</span>
    `;

        card.appendChild(row);
        card.appendChild(meta);
        gridEl.appendChild(card);
    }
}

async function fetchUsers() {
    showState("사용자 목록 불러오는 중…");
    try {
        const res = await fetch(API.USERS_FIND_ALL, { method: "GET" });
        if (!res.ok) {
            const txt = await res.text().catch(() => "");
            throw new Error(`HTTP ${res.status} ${res.statusText}\n${txt}`);
        }
        const data = await res.json();

        if (!Array.isArray(data)) {
            // 혹시 ResponseEntity로 감싸서 {data:[...]} 같은 형태면 흡수
            const maybe = data?.data ?? data?.content ?? data?.users;
            if (Array.isArray(maybe)) {
                users = maybe.map(normalizeUser);
            } else {
                throw new Error("응답이 배열이 아님. 서버 응답 구조를 확인해야 함.");
            }
        } else {
            users = data.map(normalizeUser);
        }

        apply();
    } catch (e) {
        showState(
            `불러오기 실패.\n` +
            `1) API URL 맞는지 확인: ${API.USERS_FIND_ALL}\n` +
            `2) 서버 실행 중인지 확인\n\n` +
            `에러: ${e?.message ?? e}`
        );
        users = [];
        render([]);
    }
}

function apply() {
    const q = qEl.value;
    const mode = sortEl.value;

    const filtered = filterUsers(users, q);
    const sorted = sortUsers(filtered, mode);
    render(sorted);
}

qEl.addEventListener("input", apply);
sortEl.addEventListener("change", apply);
reloadEl.addEventListener("click", fetchUsers);

fetchUsers();
