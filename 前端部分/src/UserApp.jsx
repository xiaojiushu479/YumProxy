import { Layout, Menu, Grid, message } from 'antd';
import { useState, useEffect } from 'react';
import { HomeOutlined, ShoppingCartOutlined, CloudServerOutlined, LogoutOutlined } from '@ant-design/icons';
import HomePage from './components/HomePage';
import PurchasePage from './components/PurchasePage';
import LoginForm, { RegisterForm } from './components/LoginForm';
import ServerPage from './components/ServerPage';
import ErrorNotification from './components/ErrorNotification';
import { Routes, Route, useNavigate, Navigate, useLocation } from 'react-router-dom';
import { userAPI, tokenValidationAPI, userBanAPI } from './services/api';
import './App.css';

const { useBreakpoint } = Grid;

const menuItems = [
  { key: '/home', icon: <HomeOutlined />, label: '濡絾鐗犻妴? },
  { key: '/purchase', icon: <ShoppingCartOutlined />, label: '閻犳劦鍘洪幏? },
  { key: '/server', icon: <CloudServerOutlined />, label: '闁哄牆绉存慨鐔煎闯? },
  { key: 'logout', icon: <LogoutOutlined />, label: '闂侇偀鍋撻柛鎴ｆ濞呫儴銇? },
];

export default function UserApp() {

  const savedUsername = localStorage.getItem('username');
  const [username, setUsername] = useState(savedUsername);
  const [isAuthChecking, setIsAuthChecking] = useState(false);
  const [isInitialized, setIsInitialized] = useState(!!savedUsername);
  const [lastVerifyTime, setLastVerifyTime] = useState(0);
  const [justLoggedIn, setJustLoggedIn] = useState(false);
  const isMobile = !screens.md;
  const pid = 1;
  const navigate = useNavigate();
  const location = useLocation();


  const verifyLoginStatus = async () => {


      return true;
    }
    

    const currentTime = Date.now();
    if (currentTime - lastVerifyTime < 30000) {

      return false;
    }
    
    const savedUsername = localStorage.getItem('username');
    const savedUserKey = localStorage.getItem('user_key');
    const passwordToken = localStorage.getItem('password_token');
    
    if (!savedUsername || !savedUserKey || !passwordToken) {

      handleLogout();
      return false;
    }

    try {
      setIsAuthChecking(true);
      setLastVerifyTime(currentTime);

      

        const decodedToken = atob(passwordToken);
        const tokenParts = decodedToken.split(':');
        
        if (tokenParts.length !== 2) {
          throw new Error('Token闁哄秶鍘х槐锟犳煥濞嗘帩鍤?);
        }
        
        const tokenTimestamp = parseInt(tokenParts[1]);
        
        if (isNaN(tokenTimestamp)) {
          throw new Error('Token闁哄啫鐖煎Λ鍧楀箣閾忣偅锟ラ柡?);
        }
        

        const currentTime = Date.now();
        const tokenAge = currentTime - tokenTimestamp;
        const maxAge = 7 * 24 * 60 * 60 * 1000;
        if (tokenAge > maxAge) {

          message.error('闁谎嗩嚙缂嶅秴顔忛懠鍓佺畺闁哄牏鍣︾槐婵堟嫚閻戣棄娅㈤柡鍌涘濞呫儴銇?);
          handleLogout();
          return false;
        }
        

      } catch (error) {

        message.error('闁谎嗩嚙缂嶅秵绌遍埄鍐х礀闁哄啰濮甸弲銉╂晬瀹€鍐惧殲闂佹彃绉甸弻濠囨儌鐠囪尙绉?);
        handleLogout();
        return false;
      }
      

      const response = await tokenValidationAPI.validate(savedUsername, savedUserKey);
      

      

      

      const success = responseData.success || response.status === 'success';
      const valid = responseData.valid;
      
      if (success === true && valid === true) {
        isTokenValid = true;
      }
      

        action: response.action,
        status: response.status,
        topLevelSuccess: response.success,
        dataSuccess: responseData.success,
        finalSuccess: success,
        valid: valid,
        message: responseData.message,
        username: responseData.username,
        error_code: responseData.error_code,
        user_info: responseData.user_info,
        isValid: isTokenValid,
        rawResponse: response
      });
      
            if (isTokenValid) {

        

        try {
          const banResponse = await userBanAPI.checkStatus(savedUsername, null, savedUserKey, savedUsername);

          
          const banData = banResponse.data || banResponse;
          const banSuccess = banData.success || banResponse.status === 'success';
          
          if (banSuccess && banData.is_banned === true) {

            message.error(`閻犳劧闄勯崺娑橆啅閼奸娼堕悘蹇庤兌椤? ${banData.status_message || '閻犲洨鏌夋禒鍫㈠寲閼姐値鍚€闁荤偛妫楅幉?}`);
            handleLogout();
            return false;
          }
          

        } catch (banError) {


        }
        
        setUsername(savedUsername);
        setIsInitialized(true);

        if (responseData.user_info) {

        }
        
        return true;
      } else {

        

        

          errorMsg = 'Token濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝闁? + responseData.message;
        }
        

          errorMsg += ` (${responseData.error_code})`;
        }
        
        message.error(errorMsg);
        handleLogout();
        return false;
      }
    } catch (error) {

      

        message.error('缂傚啯鍨圭划鑸垫交閻愭潙澶嶅鎯扮簿鐟欙箓鏁嶇仦鐐骏婵炲娲濈换妯兼偘鐎涚ken濡ょ姴鐭侀惁澶愭晬瀹€鍐惧殲婵☆偀鍋撻柡灞诲劤缂嶅绱掑鍐╁€甸梺鎻掔У閺屽﹦鎷嬮崸妤侊紪');
      } else {
        message.error('Token濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝闁挎稑鐭侀顒勬煂瀹ュ棙鐓€闁谎嗩嚙缂?);
      }
      
      handleLogout();
      return false;
    } finally {
      setIsAuthChecking(false);
    }
  };


    localStorage.removeItem('username');
    localStorage.removeItem('user_key');
    localStorage.removeItem('password_token');
    setUsername(null);
    setIsInitialized(false);
    navigate('/login', { replace: true });
  };

  useEffect(() => {
    const currentSavedUsername = localStorage.getItem('username');
    

      pathname: location.pathname, 
      savedUsername: currentSavedUsername,
      currentUsername: username,
      isInitialized: isInitialized,
      isAuthChecking: isAuthChecking,
      justLoggedIn: justLoggedIn
    });
    


      return;
    }
    

    if (isAuthChecking) {

      return;
    }
    

    if (location.pathname === '/login' || location.pathname === '/register') {

      return;
    }
    

    if (!currentSavedUsername) {

      navigate('/login', { replace: true });
      return;
    }
    

    if (currentSavedUsername) {

      verifyLoginStatus();
    
  }, [location.pathname]);

  const handleLoginSuccess = (newUsername) => {

    

    setUsername(newUsername);
    setIsInitialized(true);
    setJustLoggedIn(true);
    

    navigate('/home', { replace: true });
    

    setTimeout(() => {
      setJustLoggedIn(false);
    }, 3000);
  };


  const handleMenuClick = ({ key }) => {
    if (key === 'logout') {
      handleLogout();
      return;
    }
    navigate(key);
  };


    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        fontSize: '16px',
        color: '#666'
      }}>
        闁虫垝绶ょ粭?婵繐绲藉﹢顏呮交濞戞粠鏀藉ù婊勫灩椤忣剚顨ュ畝鍐...
      </div>
    );
  }


  if (!username && location.pathname !== '/login' && location.pathname !== '/register') {
    return <Navigate to="/login" replace />;
  }

  if (username && (location.pathname === '/login' || location.pathname === '/register')) {
    return <Navigate to="/home" replace />;
  }

  return (
    <>
      <ErrorNotification />
      {}
      <div style={{
        width: '100%',
        padding: '12px 24px',
        backgroundColor: 'var(--card-bg)',
        boxShadow: '0 1px 3px rgba(0,0,0,0.04)',
        position: 'sticky',
        top: 0,
        zIndex: 1000,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <div style={{ 
          fontSize: '18px',
          fontWeight: '600',
          color: 'var(--primary-color)'
        }}>
          YumProxy
        </div>
        {username && (
          <div style={{ 
            fontSize: '14px',
            color: 'var(--text-color)'
          }}>
            婵炲棎鍨肩换瀣炊閻愬瓨闄? {username}
          </div>
        )}
      </div>
      
      <Layout style={{ minHeight: '100vh', background: 'var(--light-bg)' }}>
        <div style={{ 
          maxWidth: 1200, 
          margin: isMobile ? '16px auto 0 auto' : '32px auto 0 auto', 
          width: '100%',
          padding: isMobile ? '0 16px' : '0 24px'
        }}>
          {username && (
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              marginBottom: isMobile ? 16 : 24, 
              flexDirection: isMobile ? 'column' : 'row',
              gap: isMobile ? 8 : 24
            }}>
              <Menu
                mode="horizontal"
                selectedKeys={[location.pathname]}
                onClick={handleMenuClick}
                items={menuItems}
                style={{ 
                  flex: 1, 
                  fontSize: 16, 
                  border: 'none', 
                  background: 'transparent', 
                  width: '100%',
                  borderBottom: '1px solid rgba(0,0,0,0.04)'
                }}
                className="custom-nav-menu"
              />
            </div>
          )}
          <Layout.Content className="main-content" style={{ 
            padding: 0, 
            maxWidth: 1200, 
            margin: '0 auto',
            background: 'transparent'
          }}>
            <Routes>
              <Route path="/login" element={<LoginForm onLoginSuccess={handleLoginSuccess} />} />
              <Route path="/register" element={<RegisterForm onRegisterSuccess={() => navigate('/login')} />} />
              <Route path="/home" element={<HomePage pid={pid} username={username} />} />
              <Route path="/purchase" element={<PurchasePage pid={pid} onlyCard showBindCard username={username} />} />
              <Route path="/server" element={<ServerPage pid={pid} beautify username={username} />} />
              <Route path="*" element={<Navigate to={username ? '/home' : '/login'} replace />} />
            </Routes>
          </Layout.Content>
        </div>
      </Layout>
      <style>{`
        .custom-nav-menu .ant-menu-item {
          padding: 0 16px !important;
          height: 40px;
          line-height: 40px;
          margin: 0 8px !important;
          color: var(--text-color) !important;
          transition: all 0.2s ease;
        }
        .custom-nav-menu .ant-menu-item-selected {
          background: transparent !important;
          color: var(--primary-color) !important;
          border-bottom: 2px solid var(--primary-color) !important;
          border-radius: 0 !important;
        }
        .custom-nav-menu .ant-menu-item:hover {
          color: var(--primary-color) !important;
        }
        @media (max-width: 768px) {
          .main-content { 
            padding: 0 8px !important; 
          }
          .custom-nav-menu .ant-menu-item {
            padding: 0 12px !important;
            font-size: 14px;
          }
        }
      `}</style>
    </>
  );
}
