/**
 * 校园闪电侠 · 公共函数
 * 版本: 2.0
 */

// ========== 导航栏渲染 ==========
function renderNavbar() {
    var container = document.getElementById('navbar-container');
    if (!container) return;

    var userInfo = window.app.userInfo;
    var currentRole = window.app.currentRole;
    var session = app.getCurrentUser();

    var html = `
        <div class="navbar">
            <div class="logo" onclick="location.href='${app.portalIndexUrl}'">
                <span>🏃 校园闪电侠</span>
            </div>
            <div class="nav-links">
                <a href="${app.portalIndexUrl}">首页</a>
    `;

    // 用户显示发布任务
    if (currentRole === 1) {
        html += `<a href="${app.portalIndexUrl.replace('index.html', 'publish.html')}">📝 发布任务</a>`;
    }

    // 跑腿员显示接单大厅
    if (currentRole === 2) {
        html += `<a href="${app.portalIndexUrl}?tab=available">📋 接单大厅</a>`;
    }

    // 已登录用户显示消息和钱包
    if (session) {
        html += `<a href="${app.portalIndexUrl.replace('index.html', 'messages.html')}">💬 消息</a>`;
        html += `<a href="${app.portalIndexUrl.replace('index.html', 'wallet.html')}">💰 钱包</a>`;
    }

    html += `</div>
            <div class="nav-actions">`;

    if (session && userInfo) {
        // 头像
        var userAvatar = app.getCookie("userAvatar") || "";
        if (userAvatar && userAvatar.indexOf('%3A') !== -1) {
            try { userAvatar = decodeURIComponent(userAvatar); } catch(e) {}
        }
        var userName = userInfo.nickname || '用户';
        var roleBadge = app.getRoleBadge(currentRole);

        // 头像HTML
        var avatarHtml = '';
        if (userAvatar && userAvatar !== 'null' && userAvatar !== 'undefined' && userAvatar !== '') {
            avatarHtml = `<img src="${userAvatar}" class="avatar" style="width:32px;height:32px;border-radius:50%;object-fit:cover;border:2px solid var(--border-color);flex-shrink:0;" onerror="this.style.display='none';this.parentElement.querySelector('.avatar-fallback').style.display='inline-flex';">`;
            avatarHtml += `<span class="avatar-fallback" style="display:none;align-items:center;justify-content:center;width:32px;height:32px;border-radius:50%;background:var(--primary-500);color:white;font-size:14px;font-weight:600;flex-shrink:0;">${userName.charAt(0).toUpperCase()}</span>`;
        } else {
            avatarHtml = `<span class="avatar" style="display:inline-flex;align-items:center;justify-content:center;width:32px;height:32px;border-radius:50%;background:var(--primary-500);color:white;font-size:14px;font-weight:600;flex-shrink:0;">${userName.charAt(0).toUpperCase()}</span>`;
        }

        // ===== 下拉菜单内容 =====
        var dropdownHtml = `
            <a href="${app.portalIndexUrl.replace('index.html', 'profile.html')}" style="display:block;padding:8px 16px;font-size:var(--font-size-sm);color:var(--text-primary);text-decoration:none;transition:background 0.2s;" onmouseover="this.style.background='var(--bg-hover)'" onmouseout="this.style.background='transparent'">👤 个人中心</a>
            <div style="border-top:1px solid var(--border-color);margin:4px 0;"></div>
        `;

        var runnerStatus = userInfo.runnerStatus;

        if (currentRole === 1) {
            // 普通用户
            if (runnerStatus === 1) {
                // 已通过审核 → 切换为跑腿员
                dropdownHtml += `<a href="#" onclick="app.switchToRunner();return false;" style="display:block;padding:8px 16px;font-size:var(--font-size-sm);color:var(--text-primary);text-decoration:none;transition:background 0.2s;" onmouseover="this.style.background='var(--bg-hover)'" onmouseout="this.style.background='transparent'">🏍️ 切换为跑腿员</a>`;
            } else if (runnerStatus === 3) {
                // 审核中
                dropdownHtml += `<a href="#" style="display:block;padding:8px 16px;font-size:var(--font-size-sm);color:var(--text-muted);text-decoration:none;cursor:default;">⏳ 审核中...</a>`;
            } else {
                // 未申请或已拒绝
                dropdownHtml += `<a href="#" onclick="app.applyRunner();return false;" style="display:block;padding:8px 16px;font-size:var(--font-size-sm);color:var(--primary-500);text-decoration:none;transition:background 0.2s;" onmouseover="this.style.background='var(--bg-hover)'" onmouseout="this.style.background='transparent'">🏍️ 申请成为跑腿员</a>`;
            }
        } else if (currentRole === 2) {
            // 跑腿员 → 切换为普通用户
            dropdownHtml += `<a href="#" onclick="app.switchToUser();return false;" style="display:block;padding:8px 16px;font-size:var(--font-size-sm);color:var(--text-primary);text-decoration:none;transition:background 0.2s;" onmouseover="this.style.background='var(--bg-hover)'" onmouseout="this.style.background='transparent'">👤 切换为普通用户</a>`;
        }

        dropdownHtml += `
            <div style="border-top:1px solid var(--border-color);margin:4px 0;"></div>
            <a href="#" onclick="app.logout();return false;" style="display:block;padding:8px 16px;font-size:var(--font-size-sm);color:var(--error-500);text-decoration:none;transition:background 0.2s;" onmouseover="this.style.background='var(--bg-hover)'" onmouseout="this.style.background='transparent'">🚪 退出登录</a>
        `;

        html += `
            <div class="user-menu" style="position:relative;display:inline-block;">
                <div class="user-info" style="display:flex;align-items:center;gap:8px;background:var(--bg-hover);padding:4px 12px 4px 6px;border-radius:var(--radius-full);cursor:pointer;">
                    ${avatarHtml}
                    <span style="font-size:var(--font-size-sm);">${userName}</span>
                    <span style="font-size:12px;background:var(--bg-primary);padding:1px 6px;border-radius:10px;">${roleBadge}</span>
                    <span style="font-size:12px;">▾</span>
                </div>
                <div class="user-dropdown" style="display:none;position:absolute;right:0;top:calc(100% + 4px);background:var(--bg-secondary);border:1px solid var(--border-color);border-radius:var(--radius-lg);min-width:180px;padding:4px 0;box-shadow:var(--shadow-lg);z-index:1000;">
                    ${dropdownHtml}
                </div>
            </div>
        `;
    } else {
        // 未登录
        html += `
            <a href="${app.portalIndexUrl.replace('index.html', 'login.html')}">
                <button class="btn btn-outline btn-sm">登录</button>
            </a>
            <a href="${app.portalIndexUrl.replace('index.html', 'register.html')}">
                <button class="btn btn-primary btn-sm">注册</button>
            </a>
        `;
    }

    html += `</div>
        </div>
    `;

    container.innerHTML = html;

    // 绑定下拉菜单事件
    var userInfoEl = document.querySelector('.user-info');
    var dropdown = document.querySelector('.user-dropdown');
    if (userInfoEl && dropdown) {
        userInfoEl.addEventListener('click', function(e) {
            e.stopPropagation();
            var isOpen = dropdown.style.display === 'block';
            dropdown.style.display = isOpen ? 'none' : 'block';
        });
        document.addEventListener('click', function() {
            dropdown.style.display = 'none';
        });
    }
}

// ========== 页脚渲染 ==========
function renderFooter() {
    var container = document.getElementById('footer-container');
    if (!container) return;

    container.innerHTML = `
        <footer class="footer">
            <div class="footer-content">
                <div class="footer-section">
                    <h4>关于我们</h4>
                    <a href="${app.portalIndexUrl.replace('index.html', 'about.html')}">平台介绍</a>
                    <a href="${app.portalIndexUrl.replace('index.html', 'contact.html')}">联系我们</a>
                    <a href="${app.portalIndexUrl.replace('index.html', 'help.html')}">帮助中心</a>
                </div>
                <div class="footer-section">
                    <h4>用户协议</h4>
                    <a href="#" onclick="alert('服务条款：本平台为校园跑腿服务提供技术支持。'); return false;">服务条款</a>
                    <a href="#" onclick="alert('隐私政策：我们重视您的隐私。收集的信息仅用于平台服务。'); return false;">隐私政策</a>
                </div>
                <div class="footer-section">
                    <h4>关注我们</h4>
                    <a href="#">微信公众号</a>
                    <a href="#">客服邮箱</a>
                </div>
            </div>
            <div class="footer-bottom">
                <p>© 2026 校园闪电侠 · 让校园生活更便捷</p>
            </div>
        </footer>
    `;
}

// ========== Axios配置 ==========
axios.defaults.withCredentials = true;

// 请求拦截器 - 自动添加认证头
axios.interceptors.request.use(function(config) {
    var session = app.getCurrentUser();
    if (session) {
        config.headers['headerUserId'] = session.userId;
        config.headers['headerUserToken'] = session.userToken;
    }
    return config;
}, function(error) {
    return Promise.reject(error);
});

// 响应拦截器 - 统一处理错误
axios.interceptors.response.use(function(response) {
    var data = response.data;
    if (data && data.status === 501) {
        app.showToast('请先登录');
        setTimeout(function() {
            window.location.href = app.portalIndexUrl.replace('index.html', 'login.html');
        }, 1000);
        return Promise.reject(data);
    }
    if (data && data.status === 507) {
        app.showToast('您的账号已被冻结，请联系管理员');
        return Promise.reject(data);
    }
    if (data && data.status !== 200) {
        if (data.msg) {
            app.showToast(data.msg);
        }
        return Promise.reject(data);
    }
    return response;
}, function(error) {
    if (error.response && error.response.status === 401) {
        app.showToast('登录已过期，请重新登录');
        app.clearUserInfo();
        setTimeout(function() {
            window.location.href = app.portalIndexUrl.replace('index.html', 'login.html');
        }, 1500);
    } else if (error.response && error.response.data && error.response.data.status === 507) {
        app.showToast('您的账号已被冻结，请联系管理员');
    } else {
        app.showToast('网络异常，请稍后重试');
    }
    return Promise.reject(error);
});

// ========== 页面初始化 ==========
document.addEventListener('DOMContentLoaded', function() {
    // 1. 检查是否已登录
    var session = app.getCurrentUser();
    if (!session) {
        // 未登录，直接渲染导航栏
        renderNavbar();
        renderFooter();
        // 触发页面初始化（如果页面有定义）
        if (typeof initPage === 'function') {
            initPage();
        }
        return;
    }

    // 2. 已登录，加载用户信息
    app.loadUserInfo().then(function(user) {
        renderNavbar();
        renderFooter();
        // 触发页面初始化
        if (typeof initPage === 'function') {
            initPage();
        }
    }).catch(function() {
        renderNavbar();
        renderFooter();
        if (typeof initPage === 'function') {
            initPage();
        }
    });
});