import { authAPI } from './api.js';
import { showToast, setupOrganicMotion } from './utils.js';

// Espelha Login.verificarEmail e Login.verificarSenha do back-end
const EMAIL_REGEX = /^[a-z0-9.]+@[a-z0-9]+\.[a-z]+(\.[a-z]+)?$/;
const SENHA_REGEX = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$*&@#!]).{8,}$/;

function validarEmail(email) { return EMAIL_REGEX.test(email); }
function validarSenha(senha) { return SENHA_REGEX.test(senha); }

function setFieldError(inputEl, msgEl, msg) {
    inputEl.classList.toggle('error', !!msg);
    msgEl.style.display = msg ? 'block' : 'none';
    msgEl.textContent = msg || '';
}

setupOrganicMotion();

// ── Login ─────────────────────────────────────────────────────────────────────
document.getElementById('form-login')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const senha = document.getElementById('login-senha').value;
    const btn   = e.target.querySelector('button[type=submit]');

    let ok = true;
    if (!validarEmail(email)) {
        setFieldError(
            document.getElementById('login-email'),
            document.getElementById('login-email-err'),
            'Email inválido'
        );
        ok = false;
    } else {
        setFieldError(document.getElementById('login-email'), document.getElementById('login-email-err'), '');
    }

    if (!ok) return;

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span>';

    try {
        const data = await authAPI.login(email, senha);
        sessionStorage.setItem('token', data.token);
        sessionStorage.setItem('usuario', JSON.stringify(data.usuario));
        window.location.href = 'lobby.html';
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Entrar';
    }
});

// ── Registro ──────────────────────────────────────────────────────────────────
document.getElementById('form-register')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('reg-email').value.trim();
    const senha = document.getElementById('reg-senha').value;
    const btn   = e.target.querySelector('button[type=submit]');

    let ok = true;

    if (!validarEmail(email)) {
        setFieldError(document.getElementById('reg-email'), document.getElementById('reg-email-err'), 'Email inválido');
        ok = false;
    } else {
        setFieldError(document.getElementById('reg-email'), document.getElementById('reg-email-err'), '');
    }

    if (!validarSenha(senha)) {
        setFieldError(document.getElementById('reg-senha'), document.getElementById('reg-senha-err'), 'Mínimo 8 chars, 1 maiúscula, 1 número, 1 especial ($*&@#!)');
        ok = false;
    } else {
        setFieldError(document.getElementById('reg-senha'), document.getElementById('reg-senha-err'), '');
    }

    if (!ok) return;

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span>';

    try {
        const data = await authAPI.register(email, senha);
        sessionStorage.setItem('token', data.token);
        sessionStorage.setItem('usuario', JSON.stringify(data.usuario));
        window.location.href = 'lobby.html';
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Criar conta';
    }
});

// ── Tabs ──────────────────────────────────────────────────────────────────────
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
        btn.classList.add('active');
        document.getElementById(btn.dataset.tab).classList.add('active');
    });
});
