const API_BASE = 'http://localhost:8080';

// 显示结果
function showResult(data) {
    document.getElementById('resultArea').innerHTML = `<pre style="margin:0;">${JSON.stringify(data, null, 2)}</pre>`;
}

// 注册
async function handleRegister() {
    const phone = document.getElementById('regPhone').value;
    const pwd = document.getElementById('regPwd').value;
    if(!phone || !pwd) return alert('请填写完整');
    try {
        const res = await fetch(`${API_BASE}/user/register`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({phone, password: pwd})
        });
        const json = await res.json();
        showResult(json);
        if(json.code === 200) alert('注册成功！请切换至登录Tab');
    } catch(e) { alert('请求失败'); }
}

// 登录
async function handleLogin() {
    const phone = document.getElementById('loginPhone').value;
    const pwd = document.getElementById('loginPwd').value;
    try {
        const res = await fetch(`${API_BASE}/user/login`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({phone, password: pwd})
        });
        const json = await res.json();
        showResult(json);
        if(json.code === 200 && json.data) {
            // 将 Token 存入 localStorage
            localStorage.setItem('bazaar_token', json.data);
            alert('登录成功！Token已保存');
        }
    } catch(e) { alert('请求失败'); }
}

// 获取用户信息（携带Token）
async function handleGetInfo() {
    const token = localStorage.getItem('bazaar_token');
    if(!token) return alert('请先登录！');
    try {
        const res = await fetch(`${API_BASE}/user/info`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // 关键：携带Token
            }
        });
        const json = await res.json();
        showResult(json);
    } catch(e) { alert('请求失败'); }
}