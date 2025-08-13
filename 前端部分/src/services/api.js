

const WSS_URL = 'wss:




class YumProxyWSClient {
    constructor(wsUrl) {
        this.wsUrl = wsUrl;
    this.ws = null;
        this.requestId = 1;
    this.pendingRequests = new Map();
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 1000;
        this.isManualClose = false;
        

        this.connect();
    }
    
    async ping() {

        
    }


    async connect() {
        return new Promise((resolve, reject) => {
            try {

                this.ws = new WebSocket(this.wsUrl);
                
      this.ws.onopen = () => {

                    this.reconnectAttempts = 0;
                    resolve();
                };
                
                this.ws.onerror = (error) => {

                    reject(error);
      };
      
      this.ws.onmessage = (event) => {
                    const response = JSON.parse(event.data);

                    this.handleMessage(response);
                };
                
                this.ws.onclose = (event) => {

                    

                        reject(new Error('WebSocket閺夆晝鍋炵敮鏉戭啅閹绘帒褰犻梻?));
                    });
                    this.pendingRequests.clear();
                    

                    if (!this.isManualClose && this.reconnectAttempts < this.maxReconnectAttempts) {
                        this.reconnectAttempts++;
                        const delay = this.reconnectDelay * this.reconnectAttempts;

                        setTimeout(() => {
                            this.connect();
                        }, delay);
                    }
      };
    } catch (error) {

                reject(error);
            }
        });
    }
    

        return new Promise((resolve, reject) => {


                if (this.ws && this.ws.readyState === WebSocket.CONNECTING) {

                    const checkConnection = () => {
                        if (this.ws.readyState === WebSocket.OPEN) {
                            this.sendRequest(apiType, action, data, resolve, reject);
                        } else if (this.ws.readyState === WebSocket.CLOSED || this.ws.readyState === WebSocket.CLOSING) {
                            reject(new Error('WebSocket閺夆晝鍋炵敮瀛樺緞鏉堫偉袝'));
                        } else {
                            setTimeout(checkConnection, 100);
                        }
                    };
                    checkConnection();
                } else {
                    reject(new Error('WebSocket闁哄牜浜ｇ换娑㈠箳?));
                }
                return;
            }
            
            this.sendRequest(apiType, action, data, resolve, reject);
        });
    }
    
    sendRequest(apiType, action, data, resolve, reject) {
        const requestId = (this.requestId++).toString();
        
        const request = {
            type: "api_request",
            api_type: apiType,
            action: action,
            data: data,
            request_id: requestId
        };
        

        const expectedAction = `${apiType}_${action}`;
        
        this.pendingRequests.set(requestId, { 
            resolve, 
            reject, 
            expectedAction,
            apiType,
            action
        });

        this.ws.send(JSON.stringify(request));
        

            if (this.pendingRequests.has(requestId)) {

                this.pendingRequests.delete(requestId);
                reject(new Error('閻犲洭鏀遍惇鎵惥閸涱喗顦?));
            }
        }, 60000);
    }
    
    handleMessage(response) {
        const requestId = response.request_id;
        
        if (requestId && this.pendingRequests.has(requestId)) {
            const { resolve } = this.pendingRequests.get(requestId);
        this.pendingRequests.delete(requestId);
            resolve(response);
        } else if (!requestId && response.type === 'api_response') {

            
        const pendingIds = Array.from(this.pendingRequests.keys());

            
            if (pendingIds.length === 0) {

                return;
            }
            
            let matchedRequestId = null;
            

            for (const reqId of pendingIds) {
                const pendingRequest = this.pendingRequests.get(reqId);

                if (pendingRequest && pendingRequest.expectedAction === response.action) {
                    matchedRequestId = reqId;

                    break;
                }
            }
            

            if (!matchedRequestId) {

                for (const reqId of pendingIds) {
                    const pendingRequest = this.pendingRequests.get(reqId);
                    if (pendingRequest) {
                        const { apiType, action } = pendingRequest;
                        

                        const isMatch = (
                            (apiType === 'timestamp' && (response.action?.includes('timestamp') || response.action === 'timestamp_query')) ||
                            (apiType === 'whitelist' && (response.action?.includes('whitelist') || response.action === 'whitelist_query')) ||
                            (apiType === 'user' && (response.action?.includes('user') || response.action === 'user_login')) ||
                            (response.action && response.action.includes(apiType)) ||
                            (response.action && response.action.includes(action)) ||

                        );
                        

                        
                        if (isMatch) {
                            matchedRequestId = reqId;

                            break;
                        }
                    }
                }
            }
            

                matchedRequestId = pendingIds[0];

            }
            
            if (matchedRequestId) {

                const { resolve } = this.pendingRequests.get(matchedRequestId);
                this.pendingRequests.delete(matchedRequestId);
                resolve(response);

        } else {



        }
      } else {


            

            if (response.type === 'payment_success') {

                if (window.showPaymentSuccess) {
                    window.showPaymentSuccess(response);
                }
            }
        }
    }


    async login(username, password, token) {
        return this.request('user', 'login', { username, password, token });
    }
    
    async register(username, password, email, code) {
        return this.request('user', 'register', { username, password, email, code });
    }
    
    async getUserInfo(targetUsername, userKey, superKey) {
        const data = {
            target_username: targetUsername,
            request_username: targetUsername,
            user_key: userKey
        };
        if (superKey) data.super_key = superKey;
        return this.request('user', 'get_info', data);
    }
    
    async deleteUser(targetUsername, userKey, superKey) {
        const data = {
            target_username: targetUsername,
            request_username: targetUsername,
            user_key: userKey
        };
        if (superKey) data.super_key = superKey;
        return this.request('user', 'delete', data);
    }
    
    async queryTimestamp(username, userKey) {
        return this.request('timestamp', 'query', {
            username, 
            request_username: username, 
            user_key: userKey
        });
    }
    
    async activateTimestamp(username, hours, superKey) {
        return this.request('timestamp', 'activate', {
            username,
            hours: hours.toString(),
            super_key: superKey
        });
    }
    
    async extendTimestamp(username, hours, superKey) {
        return this.request('timestamp', 'extend', {
            username,
            hours: hours.toString(),
            super_key: superKey
        });
    }
    
    async deactivateTimestamp(username, superKey) {
        return this.request('timestamp', 'deactivate', {
            username,
            super_key: superKey
        });
    }
    
    async checkTimestampActive(username, userKey) {
        return this.request('timestamp', 'check_active', {
            username,
            request_username: username,
            user_key: userKey
        });
    }
    
    async queryMinecraftId(username, userKey) {
        return this.request('whitelist', 'query', {
            username,
            request_username: username,
            user_key: userKey
        });
    }
    
    async validateMinecraftId(minecraftId) {
        return this.request('whitelist', 'validate', { minecraft_id: minecraftId });
    }
    
    async addMinecraftId(username, minecraftId, userKey) {
        return this.request('whitelist', 'add', {
            username,
            minecraft_id: minecraftId,
            request_username: username,
            user_key: userKey
        });
    }
    
    async removeMinecraftId(username, userKey) {
        return this.request('whitelist', 'remove', {
            username,
            request_username: username,
            user_key: userKey
        });
    }
    
    async getWhitelistList(superKey) {
        return this.request('whitelist', 'list', { super_key: superKey });
    }
    
    async searchWhitelist(keyword, superKey) {
        return this.request('whitelist', 'search', {
            keyword,
            super_key: superKey
        });
    }
    
    async sendEmailCode(email, token) {
        return this.request('email', 'send', { email, token });
    }

    async verifyEmailCode(email, code) {
        return this.request('email', 'verify', { email, code });
    }
    
    async queryKey(keyCode, superKey) {
        return this.request('key', 'query', {
            key: keyCode,
            super_key: superKey
        });
    }
    
    async useKey(keyCode, username) {
        return this.request('key', 'used', {
            key: keyCode,
            username
        });
    }
    

    async validateToken(username, userKey) {
        return this.request('user', 'validate_token', {
            username,
            user_key: userKey
        });
    }
    

    

    async banUser(targetUsername, reason, duration, superKey) {
        return this.request('user_ban', 'ban_user', {
            target_username: targetUsername,
            reason: reason,
            duration: duration,
            super_key: superKey
        });
    }
    

    async unbanUser(targetUsername, superKey) {
        return this.request('user_ban', 'unban_user', {
            target_username: targetUsername,
            super_key: superKey
        });
    }
    

        const data = {
            target_username: targetUsername
        };
        
        if (superKey) {
            data.super_key = superKey;
        } else if (userKey && requestUsername) {
            data.user_key = userKey;
            data.request_username = requestUsername;
        }
        
        return this.request('user_ban', 'check_ban_status', data);
    }
    

    async listBannedUsers(superKey) {
        return this.request('user_ban', 'list_banned_users', {
            super_key: superKey
        });
    }
    

    async getBanLogs(superKey, targetUsername = null, limit = 50) {
        const data = {
            super_key: superKey,
            limit: limit
        };
        
        if (targetUsername) {
            data.target_username = targetUsername;
        }
        
        return this.request('user_ban', 'get_ban_logs', data);
    }
    

    async cleanExpiredBans(superKey) {
        return this.request('user_ban', 'clean_expired_bans', {
            super_key: superKey
        });
    }
    

    

    async createPaymentOrder(type, outTradeNo, name, money, param = null, notifyUrl = null, returnUrl = null, clientip = null, device = 'pc') {
        return this.request('pay', 'create', {
            type,
            out_trade_no: outTradeNo,
            money: money.toString(),
            param,
            return_url: returnUrl,
            clientip,
            device
        });
    }
    

    async createPaymentForm(type, outTradeNo, name, money, param = null, notifyUrl = null, returnUrl = null, clientip = null, device = 'pc') {
        return this.request('pay', 'form', {
            type,
            out_trade_no: outTradeNo,
            name,
            money: money.toString(),
            param,
            notify_url: notifyUrl,
            return_url: returnUrl,
            clientip,
            device
        });
    }
    

    async handlePaymentNotify(outTradeNo, tradeStatus, sign) {
        return this.request('pay', 'notify', {
            out_trade_no: outTradeNo,
            trade_status: tradeStatus,
            sign
        });
    }
    

    async createPaymentHTTP(type, outTradeNo, name, money, param = null, notifyUrl = null, returnUrl = null, clientip = null, device = 'pc') {
        const response = await fetch('https://www.yumproxy.top/api/pay/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                type,
                out_trade_no: outTradeNo,
                name,
                money: money.toString(),
                param,
                notify_url: notifyUrl,
                return_url: returnUrl,
                clientip,
                device
            })
        });
        return await response.json();
    }
    

    async createPaymentFormHTTP(type, outTradeNo, name, money, param = null, notifyUrl = null, returnUrl = null, clientip = null, device = 'pc') {
        const response = await fetch('https://www.yumproxy.top/api/pay/form', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                type,
                out_trade_no: outTradeNo,
                name,
                money: money.toString(),
                param,
                notify_url: notifyUrl,
                return_url: returnUrl,
                clientip,
                device
            })
        });
        return await response.json();
    }
    

        const url = new URL('https://www.yumproxy.top/api/pay/query');
        if (outTradeNo) url.searchParams.set('out_trade_no', outTradeNo);
        if (status !== null) url.searchParams.set('status', status.toString());
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username,
                user_key: userKey,
                ...(superKey && { super_key: superKey })
            })
        });
        return await response.json();
    }
    

    close() {
        this.isManualClose = true;
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
    }
}




window.addEventListener('beforeunload', () => {
    client.close();
});


export const userAPI = {

    login: async (username, password, token) => {
        return client.login(username, password, token);
    },


  register: async (username, password, email, code) => {
        return client.register(username, password, email, code);
    },


    getInfo: async (targetUsername, userKey, superKey) => {
        return client.getUserInfo(targetUsername, userKey, superKey);
  },


    deleteUser: async (targetUsername, userKey, superKey) => {
        return client.deleteUser(targetUsername, userKey, superKey);
    }
};


export const emailAPI = {

    sendCode: async (email, token) => {
        return client.sendEmailCode(email, token);
    },


        return client.verifyEmailCode(email, code);
    },
};


export const qqAPI = {
    sendCode: emailAPI.sendCode,
    verifyCode: emailAPI.verifyCode
};


export const timestampAPI = {

        return client.queryTimestamp(username, userKey);
    },


        return client.activateTimestamp(username, hours, superKey);
    },


  extend: async (username, hours, superKey) => {
        return client.extendTimestamp(username, hours, superKey);
    },


  deactivate: async (username, superKey) => {
        return client.deactivateTimestamp(username, superKey);
    },


        return client.checkTimestampActive(username, userKey);
    }
};


export const whitelistAPI = {

    query: async (username, userKey) => {
        return client.queryMinecraftId(username, userKey);
    },


    validate: async (minecraftId) => {
        return client.validateMinecraftId(minecraftId);
    },


    add: async (username, minecraftId, userKey) => {
        return client.addMinecraftId(username, minecraftId, userKey);
    },


    remove: async (username, userKey) => {
        return client.removeMinecraftId(username, userKey);
    },


    list: async (superKey) => {
        return client.getWhitelistList(superKey);
    },


        return client.searchWhitelist(keyword, superKey);
    }
};


export const tokenValidationAPI = {

    validate: async (username, userKey) => {
        return client.validateToken(username, userKey);
    }
};


export const userBanAPI = {

    ban: async (targetUsername, reason, duration, superKey) => {
        return client.banUser(targetUsername, reason, duration, superKey);
    },
    

    unban: async (targetUsername, superKey) => {
        return client.unbanUser(targetUsername, superKey);
    },
    

        return client.checkBanStatus(targetUsername, superKey, userKey, requestUsername);
    },
    

    listBanned: async (superKey) => {
        return client.listBannedUsers(superKey);
    },
    

    getLogs: async (superKey, targetUsername = null, limit = 50) => {
        return client.getBanLogs(superKey, targetUsername, limit);
    },
    

    cleanExpired: async (superKey) => {
        return client.cleanExpiredBans(superKey);
    }
};


export const keyAPI = {

    query: async (keyCode, superKey) => {
        return client.queryKey(keyCode, superKey);
    },


    use: async (keyCode, username) => {
        return client.useKey(keyCode, username);
    }
};


export const paymentAPI = {

    

    createOrder: async (type, outTradeNo, name, money, param = null, notifyUrl = null, returnUrl = null, clientip = null, device = 'pc') => {
        return client.createPaymentOrder(type, outTradeNo, name, money, param, notifyUrl, returnUrl, clientip, device);
    },
    

    createForm: async (type, outTradeNo, name, money, param = null, notifyUrl = null, returnUrl = null, clientip = null, device = 'pc') => {
        return client.createPaymentForm(type, outTradeNo, name, money, param, notifyUrl, returnUrl, clientip, device);
    },
    

    handleNotify: async (outTradeNo, tradeStatus, sign) => {
        return client.handlePaymentNotify(outTradeNo, tradeStatus, sign);
    },
    

    

    createOrderHTTP: async (type, outTradeNo, name, money, param = null, notifyUrl = null, returnUrl = null, clientip = null, device = 'pc') => {
        return client.createPaymentHTTP(type, outTradeNo, name, money, param, notifyUrl, returnUrl, clientip, device);
    },
    

    createFormHTTP: async (type, outTradeNo, name, money, param = null, notifyUrl = null, returnUrl = null, clientip = null, device = 'pc') => {
        return client.createPaymentFormHTTP(type, outTradeNo, name, money, param, notifyUrl, returnUrl, clientip, device);
    },
    

    queryPayment: async (outTradeNo = null, status = null, username, userKey, superKey = null) => {
        return client.queryPaymentHTTP(outTradeNo, status, username, userKey, superKey);
    },
    

    

        const timestamp = Date.now();
        const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
        return `${prefix}${timestamp}${random}`;
    },
    

    getClientIP: async () => {
        try {
            const response = await fetch('https://api.ipify.org?format=json');
            const data = await response.json();
            return data.ip;
        } catch (error) {

            return null;
        }
    },
    

        const userAgent = navigator.userAgent;
        const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(userAgent);
        return isMobile ? 'mobile' : 'pc';
    },
    

    paymentMethods: {
        alipay: {
            name: '闁衡偓椤栨瑧甯涢悗?,
            icon: '妫ｅ啯灏?,
            description: '濞达綀娉曢弫銈夊绩椤栨瑧甯涢悗瑙勭箖婢瑰倿鎯嶆担鐟扮仐閻犲搫鐤囧ù鍡涘绩椤栨瑧甯?
        },
        wxpay: {
            name: '鐎甸偊鍠曟穱濠囧绩椤栨瑧甯?,
            icon: '妫ｅ啯瀵?,
            description: '濞达綀娉曢弫銈咁嚗椤旇绻嗛柟娈垮亞閻栨粓骞嬮弽顒傚劜閺夌儐鍓氶弫顔界?
        }
    }
};


export const playerAPI = whitelistAPI;

const api = {
  userAPI,
    emailAPI,
  qqAPI,
  timestampAPI,
    whitelistAPI,
    tokenValidationAPI,
    userBanAPI,
    keyAPI,
  paymentAPI,
    playerAPI
};

export default api;
