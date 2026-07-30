/**
 * 校园闪电侠 · 全局配置
 * 版本: 2.0
 */

window.app = {
    // 前端页面地址
    portalIndexUrl: "http://runner.gzmu.com:9091/runner/portal/index.html",
    runnerIndexUrl: "http://runner.gzmu.com:9091/runner/runner/index.html",
    adminLoginUrl: "http://runner.gzmu.com:9091/runner/admin/login.html",
    adminIndexUrl: "http://runner.gzmu.com:9091/runner/admin/index.html",

    // 后端服务地址
    userServerUrl: "http://user.runner.gzmu.com:8003",
    taskServerUrl: "http://task.runner.gzmu.com:8001",
    walletServerUrl: "http://wallet.runner.gzmu.com:8007",
    adminServerUrl: "http://admin.runner.gzmu.com:8006",
    filesServerUrl: "http://files.runner.gzmu.com:8004",

    // Cookie域名
    cookieDomain: ".runner.gzmu.com",

    // ========== 用户状态 ==========
    userInfo: null,
    currentRole: null, // 1-用户, 2-跑腿员, 3-管理员

    // ========== Cookie操作 ==========
    getCookie: function(name) {
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1);
            if (c.indexOf(name + "=") != -1) {
                var value = c.substring(name.length + 1, c.length);
                // 如果是头像URL且被编码了，解码
                if (name === 'userAvatar' && value.indexOf('%3A') !== -1) {
                    try { return decodeURIComponent(value); } catch(e) { return value; }
                }
                return value;
            }
        }
        return "";
    },

    setCookie: function(name, value, days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
            expires = "; expires=" + date.toUTCString();
        }
        var cookieValue = value;
        // 头像URL不编码，其他值编码
        if (name !== 'userAvatar') {
            cookieValue = encodeURIComponent(value);
        }
        var cookieContent = name + "=" + cookieValue + expires + "; path=/";
        if (this.cookieDomain) {
            cookieContent += "; domain=" + this.cookieDomain;
        }
        document.cookie = cookieContent;
    },

    deleteCookie: function(name) {
        var cookieContent = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/";
        if (this.cookieDomain) {
            cookieContent += "; domain=" + this.cookieDomain;
        }
        document.cookie = cookieContent;
    },

    // ========== 用户会话 ==========
    getCurrentUser: function() {
        var userId = this.getCookie("uid");
        var userToken = this.getCookie("utoken");
        if (userId && userToken) {
            return { userId: userId, userToken: userToken };
        }
        return null;
    },

    isLoggedIn: function() {
        return this.getCurrentUser() !== null;
    },

    // ========== 用户信息加载 ==========
    loadUserInfo: function() {
        var self = this;
        var userId = this.getCookie('uid');
        if (!userId) return Promise.resolve(null);

        // 先从 localStorage 读取
        var cached = this.getUserInfoFromStorage();
        if (cached && cached.id === userId) {
            self.userInfo = cached;
            self.currentRole = cached.userRole;
            // 同步更新 Cookie
            self.setCookie('userRole', cached.userRole, 30);
            if (cached.face) {
                self.setCookie('userAvatar', cached.face, 30);
            }
            if (cached.nickname) {
                self.setCookie('userName', cached.nickname, 30);
            }
            return Promise.resolve(cached);
        }

        // 从服务器获取
        return axios.post(this.userServerUrl + '/user/getUserInfo', null, {
            params: { userId: userId }
        }).then(function(res) {
            if (res.data && res.data.status === 200) {
                var user = res.data.data;
                self.userInfo = user;
                self.currentRole = user.userRole;
                self.saveUserInfoToStorage(user);
                // 更新 Cookie
                self.setCookie('userRole', user.userRole, 30);
                if (user.face) {
                    self.setCookie('userAvatar', user.face, 30);
                }
                if (user.nickname) {
                    self.setCookie('userName', user.nickname, 30);
                }
                return user;
            }
            return null;
        }).catch(function(err) {
            console.error('加载用户信息失败:', err);
            return null;
        });
    },

    saveUserInfoToStorage: function(userInfo) {
        try {
            localStorage.setItem('app_user_info', JSON.stringify(userInfo));
        } catch(e) {}
    },

    getUserInfoFromStorage: function() {
        try {
            var data = localStorage.getItem('app_user_info');
            return data ? JSON.parse(data) : null;
        } catch(e) { return null; }
    },

    clearUserInfo: function() {
        this.userInfo = null;
        this.currentRole = null;
        try {
            localStorage.removeItem('app_user_info');
        } catch(e) {}
        this.deleteCookie('uid');
        this.deleteCookie('utoken');
        this.deleteCookie('userName');
        this.deleteCookie('userRole');
        this.deleteCookie('userAvatar');
    },

    // ========== 角色判断 ==========
    isUser: function() {
        return this.currentRole === 1;
    },

    isRunner: function() {
        return this.currentRole === 2;
    },

    isAdmin: function() {
        return this.currentRole === 3;
    },

    // ========== 角色切换 ==========
    switchToRunner: function() {
        var self = this;
        if (!this.userInfo) {
            this.showToast('请先登录');
            return Promise.reject('未登录');
        }
        // 检查是否已通过审核
        return axios.post(this.userServerUrl + '/user/switchToRunner', null, {
            params: { userId: this.userInfo.id }
        }).then(function(res) {
            if (res.data && res.data.status === 200) {
                self.currentRole = 2;
                self.userInfo.userRole = 2;
                self.saveUserInfoToStorage(self.userInfo);
                self.setCookie('userRole', 2, 30);
                self.showToast('已切换为跑腿员');
                location.reload();
                return true;
            } else {
                self.showToast(res.data.msg || '切换失败');
                return false;
            }
        }).catch(function(err) {
            self.showToast(err.response ? err.response.data.msg : '切换失败');
            return false;
        });
    },

    switchToUser: function() {
        var self = this;
        if (!this.userInfo) {
            this.showToast('请先登录');
            return Promise.reject('未登录');
        }
        return axios.post(this.userServerUrl + '/user/switchToUser', null, {
            params: { userId: this.userInfo.id }
        }).then(function(res) {
            if (res.data && res.data.status === 200) {
                self.currentRole = 1;
                self.userInfo.userRole = 1;
                self.saveUserInfoToStorage(self.userInfo);
                self.setCookie('userRole', 1, 30);
                self.showToast('已切换为普通用户');
                location.reload();
                return true;
            } else {
                self.showToast(res.data.msg || '切换失败');
                return false;
            }
        }).catch(function(err) {
            self.showToast(err.response ? err.response.data.msg : '切换失败');
            return false;
        });
    },

    // ========== 申请/切换跑腿员 ==========
    applyRunner: function() {
        if (!this.userInfo) {
            this.showToast('请先登录');
            return;
        }

        var self = this;
        var userId = this.userInfo.id;

        // 先查询跑腿员状态
        axios.get(this.userServerUrl + '/passport/getRunnerStatus', {
            params: { userId: userId }
        }).then(function(res) {
            if (res.data && res.data.status === 200) {
                var data = res.data.data;
                if (data && data.status === 1) {
                    // 已通过但当前是普通用户 → 切换为跑腿员
                    self.switchToRunner();
                } else if (data && data.status === 3) {
                    self.showToast('审核中，请耐心等待');
                } else if (data && data.status === 2) {
                    // 已拒绝 → 重新申请，跳转申请页面
                    window.location.href = self.portalIndexUrl.replace('index.html', 'apply-runner.html');
                } else {
                    // 未申请 → 跳转申请页面
                    window.location.href = self.portalIndexUrl.replace('index.html', 'apply-runner.html');
                }
            } else {
                window.location.href = self.portalIndexUrl.replace('index.html', 'apply-runner.html');
            }
        }).catch(function(err) {
            window.location.href = self.portalIndexUrl.replace('index.html', 'apply-runner.html');
        });
    },

    // ========== 退出登录 ==========
    logout: function() {
        var self = this;
        var userId = this.getCookie('uid');
        if (userId) {
            axios.post(this.userServerUrl + '/passport/logout', null, {
                params: { userId: userId }
            }).catch(function() {});
        }
        this.clearUserInfo();
        window.location.href = this.portalIndexUrl;
    },

    // ========== 工具方法 ==========
    isEmpty: function(str) {
        return str == null || str == "" || str == undefined;
    },

    isNotEmpty: function(str) {
        return !this.isEmpty(str);
    },

    getUrlParam: function(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURI(r[2]);
        return null;
    },

    // ========== 日期格式化 ==========
    formatDate: function(timestamp) {
        if (!timestamp) return "";
        var date = new Date(timestamp);
        return date.getFullYear() + "-" +
            String(date.getMonth() + 1).padStart(2, '0') + "-" +
            String(date.getDate()).padStart(2, '0');
    },

    formatDateTime: function(timestamp) {
        if (!timestamp) return "";
        var date = new Date(timestamp);
        return this.formatDate(timestamp) + " " +
            String(date.getHours()).padStart(2, '0') + ":" +
            String(date.getMinutes()).padStart(2, '0');
    },

    getDateBeforeNow: function(timestamp) {
        if (!timestamp) return "";
        var now = new Date().getTime();
        var diff = now - timestamp;
        var minute = 60 * 1000;
        var hour = minute * 60;
        var day = hour * 24;
        var month = day * 30;

        if (diff < 0) return this.formatDateTime(timestamp);
        if (diff / month >= 1) return parseInt(diff / month) + "月前";
        if (diff / day >= 1) return parseInt(diff / day) + "天前";
        if (diff / hour >= 1) return parseInt(diff / hour) + "小时前";
        if (diff / minute >= 1) return parseInt(diff / minute) + "分钟前";
        return "刚刚";
    },

    // ========== Toast提示 ==========
    showToast: function(message, duration) {
        duration = duration || 2000;
        var existing = document.querySelector('.toast');
        if (existing) existing.remove();

        var toast = document.createElement('div');
        toast.className = 'toast';
        toast.textContent = message;
        document.body.appendChild(toast);

        setTimeout(function() {
            if (toast && toast.parentNode) toast.remove();
        }, duration);
    },

    // ========== 状态文本 ==========
    getStatusText: function(status) {
        var map = {
            1: '待接单',
            2: '进行中',
            3: '待确认',
            4: '已完成',
            5: '已取消'
        };
        return map[status] || status;
    },

    getStatusClass: function(status) {
        var map = {
            1: 'status-pending',
            2: 'status-accepted',
            3: 'status-delivered',
            4: 'status-completed',
            5: 'status-cancelled'
        };
        return map[status] || '';
    },

    // ========== 角色文本 ==========
    getRoleText: function(role) {
        var map = {
            1: '普通用户',
            2: '跑腿员',
            3: '管理员'
        };
        return map[role] || '未知';
    },

    // ========== 获取角色显示标签 ==========
    getRoleBadge: function(role) {
        var map = {
            1: '👤 用户',
            2: '🏍️ 跑腿员',
            3: '🔧 管理员'
        };
        return map[role] || '👤 用户';
    }
};

// 导出到全局
window.showToast = app.showToast.bind(app);
window.getStatusText = app.getStatusText.bind(app);
window.getStatusClass = app.getStatusClass.bind(app);