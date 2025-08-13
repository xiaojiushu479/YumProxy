import React, { useState } from 'react';
import { Card, Button, message, Modal, QRCode } from 'antd';
import { useNavigate } from 'react-router-dom';
import { paymentAPI, keyAPI } from '../services/api';

const plans = [
  { key: '1d', label: '闁哄啨鍎卞畷?, price: 2, hours: 24 },
  { key: '3d', label: '濞戞挸顦妵澶愬础?, price: 5, hours: 72 },
  { key: '7d', label: '闁告稏鍔屽畷?, price: 9, hours: 168 },
  { key: '30d', label: '闁哄牆鐗嗗畷?, price: 18, hours: 720 },
];


const getPaymentMethods = () => {
  return Object.entries(paymentAPI.paymentMethods).map(([key, method]) => ({
    value: key,
    label: method.name,
    icon: method.icon,
    description: method.description
  }));
};

export default function PurchasePage({ onSuccess, username }) {
  const navigate = useNavigate();
  const [buying, setBuying] = useState('');
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [qrCodeVisible, setQrCodeVisible] = useState(false);
  const [qrCodeUrl, setQrCodeUrl] = useState('');
  const [currentOrder, setCurrentOrder] = useState(null);
  const [payModalVisible, setPayModalVisible] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState(null);
  

  const [bindingCard, setBindingCard] = useState(false);
  const [lastSuccessTime, setLastSuccessTime] = useState(0);
  

  React.useEffect(() => {

    window.showPaymentSuccess = (paymentData) => {

      

      

      setBuying('');
      

      message.success(`闁衡偓椤栨瑧甯涢柟瀛樺姇婵盯鏁嶆担绛嬪悅闁告娲栬ぐ? ${paymentData.out_trade_no}`);
      

        onSuccess({
          orderNo: paymentData.out_trade_no,
          amount: paymentData.money || '闁哄牜浜為悡?,
          payType: paymentData.type,
          endTime: paymentData.endtime,
          plan: currentOrder?.plan
        });
      }
      

          navigate('/payment/success', { 
            state: { 
              paymentData: {
                ...paymentData,
                plan: currentOrder?.plan
              }
            } 
          });
        }, 2000);
    };
    

      delete window.showPaymentSuccess;
    };
  }, [onSuccess, currentOrder]);


  const handleBindCard = async () => {
    if (!cardKey.trim()) {
      message.error('閻犲洨鏌夌欢顓㈠礂閵夈儱骞㈤悗?);
      return;
    }

    if (!username) {
      message.error('閻犲洤鍢查崢娑㈡儌鐠囪尙绉?);
      return;
    }

    try {
      setBindingCard(true);

        cardKey: cardKey.trim(), 
        username,
        apiType: 'key',
        action: 'used'
      });


      const response = await keyAPI.use(cardKey.trim(), username);
      

      if (response.error_code === 'UNKNOWN_ACTION') {









      }
      


        action: response.action,
        code: response.code,
        data: response.data,
        Time: response.Time,
        message: response.message,
        error_code: response.error_code,
        status: response.status
      });
      


        
        if (response.error_code === 'UNKNOWN_ACTION') {
          message.error('闁?闁哄牆绉存慨鐔煎闯閵娿倗鐟濋柡鈧娑樼槷闁告せ鈧磭妲曢柛鏃傚枙閸忔﹢鏁嶅畝鍐惧殲闁艰鲸姊婚柈瀵哥不閿涘嫭鍊為柛?);

        } else {
          message.error(`闁?闁哄牆绉存慨鐔煎闯閵娾晜鏅╅悹? ${response.message}`);
        }
        return;
      }
      

      const responseData = response.data === 'Succeed' ? 'Succeed' : response.data?.data;
      const responseTime = response.Time || response.data?.Time;
      

        originalCode: response.code,
        dataCode: response.data?.code,
        finalCode: responseCode,
        originalData: response.data,
        dataData: response.data?.data,
        finalData: responseData,
        finalTime: responseTime
      });
      


        return;
      }
      

      if (responseCode === 200 && responseData === 'Succeed') {

        const currentTime = Date.now();
        if (currentTime - lastSuccessTime < 2000) {

          return;
        }
        

        const timeHours = responseTime || '闁哄牜浜為悡?;


        
        setLastSuccessTime(currentTime);
        message.success(`闁告せ鈧磭妲曞ù锝堟硶閺併倝骞嬮幇顒€顫犻柨娑楁祰楠炲繐顕?${timeHours} 閻忓繐绻戝鍌炲籍閸洘姣?妫ｅ啫绔碻);
        setCardKey('');
        


          onSuccess();
        }
        
        return;
      } else if (responseCode === 404 && responseData === 'Used') {


        message.error('闁?闁告せ鈧磭妲曠€规瓕灏～锔芥媴鐠恒劍鏆忛柨娑樼焷椤曨剙螞閳ь剟寮婚妷銉ュ耿閻庨潧妫欏Σ鎼佸触閿旂瓔鍔€缁?);
      } else if (responseCode === 1 && responseData === 'ERROR') {


        message.error('闁?闁告せ鈧磭妲曞☉鎾崇Т閻°劑宕烽妸锕€鐏楅柡宥囧帶缁憋繝鏌ㄥ▎鎺濆殩闁挎稑鐭侀顒€螞閳ь剟寮婚妷銉ュ耿閻庨潧妫欓悧绋款嚕?);
      } else {



        const errorMsg = response.message || '闁告せ鈧磭妲曞ù锝堟硶閺併倖寰勬潏顐バ曢柨娑樼焷椤曨剛绮欏鍛€甸梺鎻掔Х閻?;
        message.error('闁?' + errorMsg);
      }
      
    } catch (error) {

      message.error('缂備焦鍨甸悾鐐緞鏉堫偉袝: ' + (error.message || '閻犲洭顥撻埣銏ゅ触鎼淬劌娅㈤悹?));
    } finally {
      setBindingCard(false);
    }
  };


  const handleBuyClick = (planKey) => {
    setSelectedPlan(planKey);
    setPayModalVisible(true);
  };


    setPayModalVisible(false);
    setBuying(selectedPlan);
    try {
      setPaymentLoading(true);
      const plan = plans.find(p => p.key === selectedPlan);
      

      

      const deviceType = paymentAPI.getDeviceType();
      const clientIP = await paymentAPI.getClientIP();
      

      const result = await paymentAPI.createOrder(
        payType,
        orderNo,
        plan.price.toString(),
        JSON.stringify({
          planKey: selectedPlan,
          hours: plan.hours,
          timestamp: Date.now()
        }),
        `${window.location.origin}/api/payment/notify`,
        `${window.location.origin}/payment/success`,
        clientIP,
        deviceType
      );
      

      

      if (result.data && result.data.code === 1) {
        message.success('閻犱降鍨瑰畷鐔煎礆濞戞绱﹂柟瀛樺姇婵盯鏁?);
        setCurrentOrder({
          ...result.data,
          orderNo: orderNo,
          plan: plan,
          payType: payType
        });
        

        if (result.data.qrcode) {
          setQrCodeUrl(result.data.qrcode);
          setQrCodeVisible(true);
        } else if (result.data.payurl) {

          window.open(result.data.payurl, '_blank');
        } else {

        }
      } else {
        if (window.showError) {
          window.showError('闁告帗绋戠紓鎾舵媼閵忕姴绀嬪鎯扮簿鐟? ' + (result.data?.msg || result.data?.message || '闁哄牜浜為悡锟犳煥濞嗘帩鍤?));
        }
      }
    } catch (error) {

      setPaymentLoading(false);
      setBuying('');
    }
  };

  return (
    <Card style={{ borderRadius: 16, marginBottom: 16 }}>
      <h2 style={{ fontSize: 24, fontWeight: 600, marginBottom: 24, color: 'var(--text-color)' }}>闂侇偄顦扮€氥劍绺藉Δ鍜佹█</h2>
      <div style={{ 
        display: 'flex',
        flexWrap: 'nowrap',
        overflowX: 'auto',
        gap: 24,
        marginBottom: 32,
        paddingBottom: 16
      }}>
        {plans.map(plan => (
          <div key={plan.key} style={{
            background: 'var(--card-bg)',
            borderRadius: 'var(--border-radius)',
            padding: 24,
            boxShadow: '0 2px 12px rgba(0,0,0,0.04)',
            border: '1px solid rgba(0,0,0,0.04)',
            transition: 'all 0.2s ease',
            minWidth: 280,
            flex: '0 0 auto',
            ':hover': {
              transform: 'translateY(-4px)',
              boxShadow: '0 8px 24px rgba(0,0,0,0.08)'
            }
          }}>
            <div style={{ position: 'relative' }}>
            <div style={{ 
              fontSize: 20, 
              fontWeight: 600,
              marginBottom: 12,
              color: 'var(--primary-color)'
            }}>{plan.label}</div>
              
              {}
              {plan.key === '1d' && (
                <div style={{
                  position: 'absolute',
                  top: -12,
                  right: -12,
                  background: '#52c41a',
                  color: 'white',
                  fontSize: '12px',
                  padding: '4px 8px',
                  borderRadius: '8px',
                  transform: 'rotate(15deg)'
                }}>闁哄倹澹嗛弫銈夊箣?/div>
              )}
              {plan.key === '30d' && (
                <div style={{
                  position: 'absolute',
                  top: -12,
                  right: -12,
                  background: '#fa8c16',
                  color: 'white',
                  fontSize: '12px',
                  padding: '4px 8px',
                  borderRadius: '8px',
                  transform: 'rotate(15deg)'
                }}>闁哄牃鍋撻柛鎺撳笧閻?/div>
              )}
            </div>
            
            <div style={{ 
              fontSize: 32,
              fontWeight: 700,
              color: 'var(--primary-color)',
              margin: '16px 0'
            }}>濡ょ磵plan.price}</div>
            
            <div style={{ 
              color: 'var(--text-color)',
              marginBottom: 16,
              minHeight: 20
            }}>
              闁告牕鎳庨幆?<strong>{plan.hours}</strong> 閻忓繐绻戝鍌炲籍閸洘姣?
            </div>
            
            <div style={{ 
              color: '#666',
              fontSize: '14px',
              marginBottom: 24,
              minHeight: 20
            }}>
              {plan.hours === 24 ? '闂侇偄鍊搁幃搴ㄦ儗椤撶喐鍩傚ù锝嗘崌閻? : 
               plan.hours === 72 ? '闂侇偄鍊搁幃搴ㄥ川閵婏附姹炲ù锝堟硶閺? : 
               plan.hours === 168 ? '濞戞挴鍋撻柛娑栧妽濡倛绠涜婵炲洭鎮? : 
               plan.hours === 720 ? '闂傗偓閹稿孩鍩傜紒瀣暱閻ｉ箖寮靛鍛潳' : ''}
            </div>
            <Button
              type="primary"
              size="large"
              block
              loading={buying === plan.key || paymentLoading}
              onClick={() => handleBuyClick(plan.key)}
              style={{ 
                height: 48,
                fontSize: 16,
                fontWeight: 500
              }}
            >缂佹柨顑呭畵鍡欐嫻椤撴繃瀚?/Button>
          </div>
        ))}
      </div>
      {}
      <Modal
        title="閻犲洨鍏橀埀顒€顦扮€氥劑寮ㄩ娆戝笡闁哄倻鎳撶槐?
        open={payModalVisible}
        onCancel={() => setPayModalVisible(false)}
        footer={null}
        width={320}
        centered
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16, alignItems: 'center', margin: '24px 0' }}>
          {getPaymentMethods().map(method => (
            <Button
              key={method.value}
              type="primary"
              size="large"
              style={{ width: 240 }}
              onClick={() => handlePayTypeSelect(method.value)}
            >
              {method.icon} {method.label}
              <div style={{ fontSize: '12px', marginTop: 4, opacity: 0.8 }}>
                {method.description}
              </div>
            </Button>
          ))}
        </div>
      </Modal>

      {}
      <div style={{
        marginTop: 32,
        padding: 24,
        background: '#ffffff',
        borderRadius: 12,
        border: '1px solid #e8e8e8',
        boxShadow: '0 4px 16px rgba(0, 0, 0, 0.08)'
      }}>
        <h3 style={{ 
          color: '#333', 
          marginBottom: 8, 
          fontSize: 20,
          fontWeight: 600,
          textAlign: 'center'
        }}>妫ｅ啫缂?缂備焦鍨甸悾楣冨础閳ュ磭妲?/h3>
        
        <p style={{
          textAlign: 'center',
          color: '#666',
          fontSize: 14,
          marginBottom: 20,
          margin: 0
        }}>
          鐎圭寮跺﹢渚€宕￠垾宕囨闁挎稓鍠撻悵娑㈠础瀹曞洨鎷ㄩ悗瑙勭缁哄搫煤缂佹ɑ绠涢柛?        </p>
        
        <div style={{ 
          display: 'flex', 
          gap: 12, 
          alignItems: 'center',
          marginTop: 16
        }}>
          <input
            type="text"
            placeholder="閻犲洨鏌夌欢顓㈠礂閵夈儱骞㈤悗?
            value={cardKey}
            onChange={(e) => setCardKey(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && !bindingCard && handleBindCard()}
            style={{
              flex: 1,
              padding: '14px 18px',
              border: '2px solid #e8e8e8',
              borderRadius: 10,
              fontSize: 16,
              background: '#ffffff',
              color: '#333',
              outline: 'none',
              transition: 'all 0.3s ease'
            }}
            disabled={bindingCard}
          />
          <Button
            type="primary"
            size="large"
            onClick={handleBindCard}
            loading={bindingCard}
            style={{
              height: 50,
              padding: '0 28px',
              background: '#1890ff',
              border: 'none',
              borderRadius: 10,
              fontWeight: 600,
              fontSize: 16,
              boxShadow: '0 4px 15px rgba(24, 144, 255, 0.3)'
            }}
          >
            {bindingCard ? '缂備焦鍨甸悾鐐▔?..' : '缂佹柨顑呭畵鍡欑磼閹存繄鏆?}
          </Button>
        </div>
      </div>

      {}
      <div style={{
        marginTop: 24,
        padding: 16,
        background: '#f5f5f5',
        borderRadius: 8,
        textAlign: 'center'
      }}>
        <div style={{ fontWeight: 500 }}>闁哥儐鍠栭幃妗籕缂傚洢鍊х槐?23802006</div>
        <div style={{ fontSize: 12, color: '#666', marginTop: 4 }}>濠碘€冲€瑰﹢渚€姊婚鈧。鐣屾嫚閻ゎ垯绮撶紒顖濐嚙椤撳綊寮?/div>
      </div>

      {}
      {currentOrder && (
        <Card title="鐟滅増鎸告晶鐘垫媼閵忕姴绀? style={{ marginTop: '16px' }}>
          <p><strong>闁绘鍩栭埀?</strong> {currentOrder.status === 'success' ? '闁告帗绋戠紓鎾诲箣閹邦剙顫? : '闁告帗绋戠紓鎾村緞鏉堫偉袝'}</p>
          <p><strong>闁哄啫鐖煎Λ?</strong> {new Date(currentOrder.timestamp).toLocaleString()}</p>
          {currentOrder.data?.trade_no && (
            <p><strong>閻犱降鍨瑰畷鐔煎矗?</strong> {currentOrder.data.trade_no}</p>
          )}
          {currentOrder.data?.money && (
            <p><strong>闂佸弶鍨块·?</strong> 濡ょ磵currentOrder.data.money}</p>
          )}
          {currentOrder.data?.payurl && (
            <Button 
              type="link" 
              onClick={() => window.open(currentOrder.data.payurl, '_blank')}
            >
              闁绘劗鎳撻崵顕€寮ㄩ娆戝笡
            </Button>
          )}
          {!currentOrder.data?.payurl && !currentOrder.data?.qrcode && (
            <p style={{ color: '#888', fontSize: '14px' }}>
              缂佹稑顦欢鐔煎嫉瀹ュ懎顫ら柛锝冨姀缁绘垿宕堕悙瀛樻殰濞寸姵眉娣囧﹪骞?..
            </p>
          )}
        </Card>
      )}

      {}
      <Modal
        title="闁规鍋嗛悥婊堝绩椤栨瑧甯?
        open={qrCodeVisible}
        onCancel={() => setQrCodeVisible(false)}
        footer={null}
        width={400}
        centered
      >
        <div style={{ textAlign: 'center', padding: '20px 0' }}>
          <QRCode value={qrCodeUrl} size={200} />
          
          {currentOrder && (
            <div style={{ marginTop: 16 }}>
              <p style={{ fontSize: '16px', fontWeight: 'bold', margin: '8px 0' }}>
                {currentOrder.plan?.label || '闁哥喎妫楅幖?}
              </p>
              <p style={{ fontSize: '18px', color: '#f5222d', margin: '8px 0' }}>
                濡ょ磵currentOrder.plan?.price || '闁哄牜浜為悡?}
              </p>
            </div>
          )}
          
          <p style={{ marginTop: 16, color: '#666' }}>
            閻犲洩娓规繛鍥偨閳诲垻urrentOrder?.payType === 'alipay' ? '闁衡偓椤栨瑧甯涢悗? : '鐎甸偊鍠曟穱?}闁规鍋嗛悥婊堝绩椤栨瑧甯?
          </p>
          
          <div style={{ 
            background: '#f0f8ff', 
            padding: '12px', 
            borderRadius: '6px', 
            margin: '16px 0',
            fontSize: '14px',
            color: '#1890ff'
          }}>
            妫ｅ啯瀵?闁衡偓椤栨瑧甯涢悗鐟版湰閸ㄦ岸宕ユ惔婵堢獥闁煎浜滄慨鈺佄涢埀顒€霉鐎ｎ亣瀚欓悹鍝勭枃濞村棝鏁嶅畝鍐惧殲濞ｅ洦绻冪€垫梹銇勯悽鍛婃〃闁瑰灚鎸哥槐?
          </div>
          
          <p style={{ fontSize: '12px', color: '#999' }}>
            閻犱降鍨瑰畷鐔煎矗? {currentOrder?.orderNo}
          </p>
        </div>
      </Modal>
    </Card>
  );
}
