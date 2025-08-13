import React, { useState } from 'react';
import { Input, Button, message, Form } from 'antd';
import { UserOutlined, LockOutlined, QqOutlined } from '@ant-design/icons';
import { userAPI, emailAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';
import Turnstile, { useTurnstile } from 'react-turnstile';



const LoginForm = ({ onLoginSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [loginForm] = Form.useForm();
  const navigate = useNavigate();
  useTurnstile();
  let token = '';
  let isVerified = false;

  const processTurnstileToken = async (t) => {
    isVerified = true;
    token = t;
  }


  const handleLogin = async (values) => {
    if (!isVerified) {
      const errorMsg = '閻犲洤鍢查崢娑氣偓鐟版湰閸ㄦ碍绂嶉悜妯荤皻濡ょ姴鐭侀惁?;
      if (window.showError) {
        window.showError(errorMsg);
      } else {
        message.error(errorMsg);
      }
      return;
    }
    try {
      setLoading(true);
      const response = await userAPI.login(values.username, values.password, token);


      

      let isLoginSuccess = false;
      
      if (response.status === 'success') {

        if (response.data) {

          if (response.data.code !== undefined) {
            isLoginSuccess = response.data.code === 200;
          }

            isLoginSuccess = !response.data.message.includes('failed') && 
                           !response.data.message.includes('濠㈡儼绮剧憴?) &&
                           !response.data.message.includes('闂佹寧鐟ㄩ?);
          }

            isLoginSuccess = !response.data.data.includes('failed') && 
                           !response.data.data.includes('濠㈡儼绮剧憴?) &&
                           !response.data.data.includes('闂佹寧鐟ㄩ?);
          }
          else {

            isLoginSuccess = true;
            isVerified = true;
          }
        } else {

          isLoginSuccess = !response.message || 
                          (!response.message.includes('failed') && 
                           !response.message.includes('濠㈡儼绮剧憴?) &&
                           !response.message.includes('闂佹寧鐟ㄩ?));
        }
      }
      

        status: response.status,
        message: response.message,
        dataCode: response.data?.code,
        dataMessage: response.data?.message,
        dataData: response.data?.data,
        isLoginSuccess: isLoginSuccess,
      });
      
      if (isLoginSuccess) {

        localStorage.setItem('username', values.username);
        


        const passwordWithTimestamp = `${values.password}:${timestamp}`;
        const passwordToken = btoa(passwordWithTimestamp);
        localStorage.setItem('password_token', passwordToken);
        

        let userKey = null;
        if (response.data && response.data.user_key) {
          userKey = response.data.user_key;
        } else if (response.user_key) {
          userKey = response.user_key;
        } else if (response.data && response.data.userKey) {
          userKey = response.data.userKey;
        } else if (response.userKey) {
          userKey = response.userKey;
        }
        
        if (userKey) {
          localStorage.setItem('user_key', userKey);

        } else {

        }
        

        if (onLoginSuccess) {

        }
      } else {

        let errorMsg = '闁谎嗩嚙缂嶅秵寰勬潏顐バ曢柨娑樼焷椤曨剙螞閳ь剟寮婚妷褎鏆忛柟鏉戝槻閹洟宕仦鐣屾闁?;
        

        if (response.data) {
          if (response.data.message && response.data.message !== '') {
            errorMsg = response.data.message;
          } else if (response.data.data && response.data.data !== '') {
            errorMsg = response.data.data;
          }
        }

        else if (response.message) {
          errorMsg = response.message;
        }
        

        if (window.showError) {
          window.showError(errorMsg);
        } else {
          message.error(errorMsg);
        }
      }
    } catch (error) {

      const errorMsg = error.message || '闁谎嗩嚙缂嶅秵寰勬潏顐バ曢柨娑樼焷椤曨剙螞閳ь剟寮婚妷褏绉圭紓浣圭矎缁绘盯骞?;
      if (window.showError) {
        window.showError(errorMsg);
      } else {
        message.error(errorMsg);
      }
    } finally {
      setLoading(false);
    }
  };



  return (
    <div style={{
      position: 'fixed',
      left: 0,
      top: 0,
      width: '100vw',
      height: '100vh',
      minHeight: '100vh',
      minWidth: '100vw',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      background: 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)',
      overflow: 'hidden',
      zIndex: 0,
    }}>
      <div style={{
        maxWidth: 480,
        width: '100%',
        minHeight: 450,
        aspectRatio: '4/3',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        padding: '32px',
      }}>
        <div style={{ textAlign: 'left', marginBottom: 12, width: '100%' }}>
          <h1 style={{ fontWeight: 700, fontSize: 28, color: '#222', marginBottom: 8 }}>婵炲棎鍨肩换瀣媴鐠恒劍鏆廦umProxy</h1>
          <div style={{ color: '#888', fontSize: 16, marginBottom: 24 }}>闁谎嗩嚙缂嶅秵鎷呴悩鍨暠閻犳劧绠戣ぐ?/div>
        </div>
        <Form
          form={loginForm}
          onFinish={handleLogin}
          layout="vertical"
          style={{ width: '100%', maxWidth: 420, margin: '0 auto' }}
        >
          <Form.Item
            label="闁活潿鍔嶉崺娑㈠触瀹ュ繒绐?
            name="username"
            rules={[{ required: true, message: '閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃? }]}
            style={{ marginBottom: 18 }}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="闁活潿鍔嶉崺娑㈠触?
              size="large"
              style={{ width: '100%' }}
            />
          </Form.Item>
          <Form.Item
            label="閻庨潧妫涢悥婊堟晬?
            name="password"
            rules={[{ required: true, message: '閻犲洨鏌夌欢顓㈠礂閵夈儳妲曢柣? }]}
            style={{ marginBottom: 18 }}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="閻庨潧妫涢悥?
              size="large"
              style={{ width: '100%' }}
            />
          </Form.Item>
          <Form.Item>
            <Turnstile
              sitekey={ SITE_KEY }
              onVerify={ processTurnstileToken }
            />
          </Form.Item>
          <Form.Item style={{ marginBottom: 24 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              size="large"
              style={{ width: '100%', fontWeight: 600 }}
            >
              闁谎嗩嚙缂?
            </Button>
          </Form.Item>
        </Form>
        <div style={{ textAlign: 'right', marginTop: 8, width: '100%' }}>
          <span style={{ color: '#888', fontSize: 15 }}>婵炲备鍓濆﹢浣烘嫻閿曗偓瑜板潡鏁?/span>
          <Button type="link" style={{ padding: 0, fontSize: 15 }} onClick={() => navigate('/register')}>闁绘劗顢婄换鏍煂鐏炵偓鏆堥柛?/Button>
        </div>
      </div>
    </div>
  );
};

const RegisterForm = ({ onRegisterSuccess }) => {
  const [registerLoading, setRegisterLoading] = useState(false);
  const [sendCodeLoading, setSendCodeLoading] = useState(false);
  const [registerForm] = Form.useForm();
  const [emailCode, setEmailCode] = useState('');
  const navigate = useNavigate();
  useTurnstile();

  let token;
  let isVerified = false;

  const processTurnstileToken = async (t) => {
    isVerified = true;
    token = t;
  }


    const trimmed = input.trim();

    return trimmed;
  };


    if (!qq) return false;
    

    return /^\d{5,11}$/.test(qq.trim());
  };


  const handleSendCode = async () => {
    const qq = registerForm.getFieldValue('qq');
    if (!qq) {
      const errorMsg = '閻犲洤鍢查崢娑欐綇閹惧啿寮砆Q闁?;
      if (window.showError) {
        window.showError(errorMsg);
      } else {
        message.error(errorMsg);
      }
      return;
    }

    if (!validateQQ(qq)) {
      const errorMsg = '閻犲洨鏌夌欢顓㈠礂閵夛富鍔€缁绢収鍠氬▓鎱烸闁告瑥鍤栫槐?-11濞达絽绉甸弳鐔衡偓娑欘殣缁?;
      if (window.showError) {
        window.showError(errorMsg);
      } else {
        message.error(errorMsg);
      }
      return;
    }
    
    try {
      if (!isVerified) {
        const errorMsg = '閻犲洤鍢查崢娑氣偓鐟版湰閸ㄦ碍绂嶉悜妯荤皻濡ょ姴鐭侀惁?;
        if (window.showError) {
          window.showError(errorMsg);
        } else {
          message.error(errorMsg);
        }
        return;
      }
      setSendCodeLoading(true);
      const processedQQ = processQQInput(qq);

      
      const response = await emailAPI.sendCode(processedQQ, token);
      

        const displayEmail = response.data.email || `${processedQQ}@qq.com`;
        message.success(`濡ょ姴鐭侀惁澶愭儘娴ｇ鍤掗柛娆愬灴閳ь兛绀侀崺瀛甉闂侇収鍠氶?${displayEmail}`);
        isVerified = true;
      } else if (response.status === 'success') {

      } else {
        const errorMsg = response.message || response.data?.message || '闁告瑦鍨块埀顑跨窔閻涙瑧鎷犳担铏瑰灣濠㈡儼绮剧憴?;
        if (window.showError) {
          window.showError(errorMsg);
        } else {
          message.error(errorMsg);
        }
      }
    } catch (error) {

      const errorMsg = error.message || '闁告瑦鍨块埀顑跨窔閻涙瑧鎷犳担铏瑰灣濠㈡儼绮剧憴?;
      if (window.showError) {
        window.showError(errorMsg);
      } else {
        message.error(errorMsg);
      }
    } finally {
      setSendCodeLoading(false);
    }
  };


  const handleRegister = async (values) => {
    if (!emailCode) {
      const errorMsg = '閻犲洨鏌夌欢顓㈠礂閵夆晝宕ｉ悹鍥﹁兌閻?;
      if (window.showError) {
        window.showError(errorMsg);
      } else {
        message.error(errorMsg);
      }
      return;
    }

    if (!validateQQ(values.qq)) {
      const errorMsg = '閻犲洨鏌夌欢顓㈠礂閵夛富鍔€缁绢収鍠氬▓鎱烸闁告瑥鍤栫槐?-11濞达絽绉甸弳鐔衡偓娑欘殣缁?;
      if (window.showError) {
        window.showError(errorMsg);
      } else {
        message.error(errorMsg);
      }
      return;
    }
    
    try {
      setRegisterLoading(true);
      const processedQQ = processQQInput(values.qq);

        username: values.username, 
        qq: processedQQ, 
        code: emailCode 
      });
      
      const response = await userAPI.register(values.username, values.password, processedQQ, emailCode);
      

        message.success('婵炲鍔岄崬浠嬪箣閹邦剙顫犻柨娑楁祰椤曨剟鎯傜拠鑼Э');
        registerForm.resetFields();
        setEmailCode('');
        

        localStorage.removeItem('username');
        localStorage.removeItem('user_key');
        localStorage.removeItem('password_token');
        

        if (response.data.user_key) {
          localStorage.setItem('user_key', response.data.user_key);

        }
        
        if (onRegisterSuccess) {
          onRegisterSuccess();
        }
      } else if (response.status === 'success') {

        registerForm.resetFields();
        setEmailCode('');
        

        localStorage.removeItem('username');
        localStorage.removeItem('user_key');
        localStorage.removeItem('password_token');
        

        if (response.data && response.data.user_key) {
          localStorage.setItem('user_key', response.data.user_key);

        }
        
        if (onRegisterSuccess) {
          onRegisterSuccess();
        }
      } else {
        const errorMsg = response.message || response.data?.message || '婵炲鍔岄崬鑺ュ緞鏉堫偉袝闁挎稑鐭侀顒€螞閳ь剟寮婚妷銈勭箚闁?;
        if (window.showError) {
          window.showError(errorMsg);
        } else {
          message.error(errorMsg);
        }
      }
    } catch (error) {

      const errorMsg = error.message || '婵炲鍔岄崬鑺ュ緞鏉堫偉袝闁挎稑鐭侀顒€螞閳ь剟寮婚妷褏绉圭紓浣圭矎缁绘盯骞?;
      if (window.showError) {
        window.showError(errorMsg);
      } else {
        message.error(errorMsg);
      }
    } finally {
      setRegisterLoading(false);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      left: 0,
      top: 0,
      width: '100vw',
      height: '100vh',
      minHeight: '100vh',
      minWidth: '100vw',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      background: 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)',
      overflow: 'hidden',
      zIndex: 0,
    }}>
      <div style={{
        maxWidth: 480,
        width: '100%',
        minHeight: 450,
        aspectRatio: '4/3',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        padding: '32px',
      }}>
        <div style={{ textAlign: 'left', marginBottom: 12, width: '100%' }}>
          <h1 style={{ fontWeight: 700, fontSize: 28, color: '#222', marginBottom: 8 }}>婵炲棎鍨肩换瀣枖閵娿儱鏂€YumProxy</h1>
          <div style={{ color: '#888', fontSize: 16, marginBottom: 24 }}>濞达綀娉曢弫顥稱闁告瑥鍢查幓鈺呮焻閻斿憡鏆堥柛?/div>
        </div>
        <Form
          method='POST'
          form={registerForm}
          onFinish={handleRegister}
          layout="vertical"
          style={{ width: '100%', maxWidth: 420, margin: '0 auto' }}
        >
          <Form.Item
            label="闁活潿鍔嶉崺娑㈠触瀹ュ繒绐?
            name="username"
            rules={[{ required: true, message: '閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃? }]}
            style={{ marginBottom: 22 }}
          >
            <Input prefix={<UserOutlined />} placeholder="闁活潿鍔嶉崺娑㈠触? size="large" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            label="閻庨潧妫涢悥婊堟晬?
            name="password"
            rules={[{ required: true, message: '閻犲洨鏌夌欢顓㈠礂閵夈儳妲曢柣? }]}
            style={{ marginBottom: 22 }}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="閻庨潧妫涢悥? size="large" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            label="QQ闁告瑥鍤栫槐?
            name="qq"
            rules={[
              { required: true, message: '閻犲洨鏌夌欢顓㈠礂椤ф』闁? },
              {
                validator: (_, value) => {
                  if (!value) return Promise.resolve();
                  if (validateQQ(value)) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('閻犲洨鏌夌欢顓㈠礂閵夛富鍔€缁绢収鍠氬▓鎱烸闁告瑥鍤栫槐?-11濞达絽绉甸弳鐔衡偓娑欘殣缁?));
                }
              }
            ]}
            style={{ marginBottom: 22 }}
            extra={
              <div style={{ fontSize: 12, color: '#666', marginTop: 4 }}>
                閻犲洨鏌夌欢顓㈠礂閵夛箑浜堕柣銊ュ殽Q闁告瑥鍤栫槐婵堝寲閼姐倗鍩犻悘蹇撴閸ゆ粓宕濋妸銉ョ岛闂侇偂绶氶悰娆戞嫚娴ｈ櫣鍨抽柛鎺撴緲椤曨喗鎯旈弮鍌涚暠QQ闂侇収鍠氶?
              </div>
            }
          >
            <Input 
              prefix={<QqOutlined />} 
              placeholder="閻犲洨鏌夌欢顓㈠礂閵夛箑浜堕柣銊ュ殽Q闁告瑥鍤栫槐婵囦繆閸岋妇绐?23456789" 
              size="large" 
              style={{ width: '100%' }}
              maxLength={11}
            />
          </Form.Item>
          <Form.Item style={{ marginBottom: 22 }}>
            <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
              <Input
                placeholder="濡ょ姴鐭侀惁澶愭儘?
                value={emailCode}
                onChange={(e) => setEmailCode(e.target.value)}
                style={{ flex: 1, minWidth: 0, borderRadius: 8 }}
                size="large"
              />
              <Button 
                onClick={handleSendCode} 
                loading={sendCodeLoading} 
                size="large" 
                style={{ minWidth: 120, borderRadius: 8 }}
              >
                闁告瑦鍨块埀顑跨窔閻涙瑧鎷犳担铏瑰灣
              </Button>
            </div>
          </Form.Item>
          <Turnstile
            sitekey={SITE_KEY}
            onVerify={ processTurnstileToken }
          />
          <Form.Item style={{ marginBottom: 28 }}>
            <Button type="primary" htmlType="submit" loading={registerLoading} size="large" style={{ width: '100%', fontWeight: 600 }}>
              婵炲鍔岄崬?
            </Button>
          </Form.Item>
        </Form>
        <div style={{ textAlign: 'right', marginTop: 8, width: '100%' }}>
          <span style={{ color: '#888', fontSize: 15 }}>鐎圭寮跺﹢浣烘嫻閿曗偓瑜板潡鏁?/span>
          <Button type="link" style={{ padding: 0, fontSize: 15 }} onClick={() => navigate('/login')}>閺夆晜鏌ㄥú鏍儌鐠囪尙绉?/Button>
        </div>
      </div>
    </div>
  );
};

export { RegisterForm };
export default LoginForm;
