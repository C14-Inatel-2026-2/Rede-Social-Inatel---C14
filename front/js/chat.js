import { ChatSocket } from './socket.js';
import { requireAuth, setupOrganicMotion } from './utils.js';

requireAuth();

const sala    = JSON.parse(sessionStorage.getItem('sala') || 'null');
const usuario = JSON.parse(sessionStorage.getItem('usuario') || '{}');

if (!sala) { window.location.href = 'lobby.html'; }

// ── UI refs ───────────────────────────────────────────────────────────────────
const messagesEl  = document.getElementById('messages');
const inputEl     = document.getElementById('msg-input');
const sendBtn     = document.getElementById('send-btn');
const roomNameEl  = document.getElementById('room-name');
const usersCountEl= document.getElementById('users-count');

roomNameEl.textContent = sala.nome;
setupOrganicMotion();

// ── Mensagens ─────────────────────────────────────────────────────────────────
function appendMsg({ tipo, autor, conteudo }) {
    const el = document.createElement('div');

    if (tipo === 'sistema') {
        el.className = 'msg system';
        el.textContent = conteudo;
    } else {
        const isMine = autor === usuario.email;
        el.className = `msg ${isMine ? 'sent' : 'received'}`;
        el.innerHTML = `<div class="msg-author">${autor}</div>${conteudo}`;
    }

    messagesEl.appendChild(el);
    messagesEl.scrollTop = messagesEl.scrollHeight;
}

// ── Socket ────────────────────────────────────────────────────────────────────
let socket = null;

function conectar() {
    socket = new ChatSocket(sala.id, sala.porta);

    socket
        .on('open', () => {
            usersCountEl.textContent = '● conectado';
            appendMsg({ tipo: 'sistema', conteudo: `Você entrou em ${sala.nome}` });
            sendBtn.disabled = false;
        })
        .on('message', (data) => {
            // data: { tipo, autor, conteudo, quantidadeUsuarios? }
            if (data.quantidadeUsuarios !== undefined) {
                usersCountEl.textContent = `${data.quantidadeUsuarios}/12 online`;
            }
            appendMsg(data);
        })
        .on('close', () => {
            usersCountEl.textContent = '● desconectado';
            sendBtn.disabled = true;
            appendMsg({ tipo: 'sistema', conteudo: 'Conexão encerrada.' });
        })
        .on('error', () => {
            appendMsg({ tipo: 'sistema', conteudo: 'Erro de conexão.' });
        });
}

// ── Envio ─────────────────────────────────────────────────────────────────────
function enviar() {
    const texto = inputEl.value.trim();
    if (!texto || !socket) return;
    socket.enviar(texto);
    inputEl.value = '';
    inputEl.style.height = 'auto';
}

sendBtn.addEventListener('click', enviar);

inputEl.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); enviar(); }
});

inputEl.addEventListener('input', () => {
    inputEl.style.height = 'auto';
    inputEl.style.height = inputEl.scrollHeight + 'px';
});

// ── Voltar ────────────────────────────────────────────────────────────────────
document.getElementById('back-btn').addEventListener('click', () => {
    socket?.fechar();
    window.location.href = 'lobby.html';
});

// ── Demo (sem back-end) ───────────────────────────────────────────────────────
// Remove o bloco abaixo quando o back estiver pronto
function demoMode() {
    usersCountEl.textContent = '5/12 online';
    sendBtn.disabled = false;
    appendMsg({ tipo: 'sistema', conteudo: `Você entrou em ${sala.nome} [modo demo]` });
    appendMsg({ tipo: 'texto', autor: 'joao@email.com', conteudo: 'Oi pessoal! 👋' });
    appendMsg({ tipo: 'texto', autor: 'maria@email.com', conteudo: 'Olá! Bem-vindo!' });

    sendBtn.addEventListener('click', () => {}, { once: true }); // já registrado acima
}

try {
    conectar();
} catch {
    demoMode();
}
