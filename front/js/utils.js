// ── Toast ─────────────────────────────────────────────────────────────────────
export function showToast(msg, type = 'info') {
    let toast = document.getElementById('toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast';
        toast.className = 'toast';
        document.body.appendChild(toast);
    }
    toast.textContent = msg;
    toast.className = `toast ${type} show`;
    clearTimeout(toast._t);
    toast._t = setTimeout(() => toast.classList.remove('show'), 3000);
}

// ── Motions orgânicas e interativas ─────────────────────────────────────────────
export function setupOrganicMotion() {
    const targets = document.querySelectorAll(
        '.auth-box, .room-card, .modal, .chat-container, .chat-header, .chat-input-area, .btn, .user-btn, .send-btn'
    );

    targets.forEach((el) => {
        el.addEventListener('pointermove', (event) => {
            const rect = el.getBoundingClientRect();
            const relX = (event.clientX - rect.left) / rect.width;
            const relY = (event.clientY - rect.top) / rect.height;
            const rotateY = (relX - 0.5) * 8;
            const rotateX = (0.5 - relY) * 8;
            el.style.transform = `perspective(900px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-2px)`;
        });

        el.addEventListener('pointerleave', () => {
            el.style.transform = '';
        });

        el.addEventListener('pointerdown', (event) => {
            const rect = el.getBoundingClientRect();
            const circle = document.createElement('span');
            const size = Math.max(rect.width, rect.height) * 1.2;
            circle.className = 'ripple';
            circle.style.width = `${size}px`;
            circle.style.height = `${size}px`;
            circle.style.left = `${event.clientX - rect.left - size / 2}px`;
            circle.style.top = `${event.clientY - rect.top - size / 2}px`;
            el.appendChild(circle);
            setTimeout(() => circle.remove(), 500);
        });
    });
}

// ── Auth guard ────────────────────────────────────────────────────────────────
export function requireAuth() {
    if (!sessionStorage.getItem('token')) {
        window.location.href = 'index.html';
    }
}

// ── Logout ────────────────────────────────────────────────────────────────────
export function logout() {
    sessionStorage.clear();
    window.location.href = 'index.html';
}
