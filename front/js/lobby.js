import { salasAPI } from './api.js';
import { showToast, requireAuth, logout, setupOrganicMotion } from './utils.js';

requireAuth();

const usuario = JSON.parse(sessionStorage.getItem('usuario') || '{}');
setupOrganicMotion();

// ── Perfil ────────────────────────────────────────────────────────────────────
document.getElementById('profile-email').textContent = usuario.email || '—';
document.getElementById('user-btn').addEventListener('click', () => {
    document.getElementById('profile-modal').classList.add('open');
});
document.getElementById('modal-close').addEventListener('click', () => {
    document.getElementById('profile-modal').classList.remove('open');
});
document.getElementById('btn-logout').addEventListener('click', logout);

// ── Salas ─────────────────────────────────────────────────────────────────────
const grid = document.getElementById('rooms-grid');

function renderSalas(salas) {
    grid.innerHTML = '';
    salas.forEach(sala => {
        const full = sala.quantidadeUsuarios >= sala.capacidadeMaxima;
        const pct  = (sala.quantidadeUsuarios / sala.capacidadeMaxima) * 100;

        const card = document.createElement('div');
        card.className = `room-card${full ? ' full' : ''}`;
        card.innerHTML = `
      <h3>${sala.nome}</h3>
      <div class="room-meta">
        <span class="dot"></span>
        <span class="users-count">${sala.quantidadeUsuarios}/${sala.capacidadeMaxima} online</span>
      </div>
      <div class="capacity-bar">
        <div class="capacity-fill" style="width:${pct}%"></div>
      </div>
    `;

        if (!full) {
            card.addEventListener('click', () => entrarSala(sala.id, sala.nome));
        }

        grid.appendChild(card);
    });
}

async function entrarSala(id, nome) {
    try {
        const { porta } = await salasAPI.entrar(id);
        sessionStorage.setItem('sala', JSON.stringify({ id, nome, porta }));
        window.location.href = 'chat.html';
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function carregarSalas() {
    grid.innerHTML = '<p style="color:var(--muted);grid-column:1/-1">Carregando salas…</p>';
    try {
        const salas = await salasAPI.listar();
        renderSalas(salas);
    } catch {
        // Modo demo — sem back-end conectado
        renderSalas([
            { id: 1, nome: 'Sala Geral',    quantidadeUsuarios: 5,  capacidadeMaxima: 12 },
            { id: 2, nome: 'Sala Jogos',    quantidadeUsuarios: 12, capacidadeMaxima: 12 },
            { id: 3, nome: 'Sala Música',   quantidadeUsuarios: 3,  capacidadeMaxima: 12 },
            { id: 4, nome: 'Sala Estudos',  quantidadeUsuarios: 0,  capacidadeMaxima: 12 },
            { id: 5, nome: 'Sala Filmes',   quantidadeUsuarios: 8,  capacidadeMaxima: 12 },
            { id: 6, nome: 'Sala Aleatória',quantidadeUsuarios: 1,  capacidadeMaxima: 12 },
        ]);
    }
}

carregarSalas();
// Atualiza a cada 15s
setInterval(carregarSalas, 15000);
