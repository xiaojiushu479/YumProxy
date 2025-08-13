import React, { useState } from 'react';
import { Card, Button, Radio, Input, message, Modal, QRCode, Space, Typography } from 'antd';
import { paymentAPI } from '../services/api';

const { Title, Text } = Typography;

export default function PaymentTestPage() {
  const [loading, setLoading] = useState(false);
  const [payMethod, setPayMethod] = useState('alipay');
  const [amount, setAmount] = useState('2.00');
  const [productName, setProductName] = useState('婵炴潙顑堥惁顖炲疮閸℃鎯?);
  const [qrVisible, setQrVisible] = useState(false);
  const [payUrl, setPayUrl] = useState('');
  const [payResult, setPayResult] = useState(null);


  React.useEffect(() => {
    window.showPaymentSuccess = (paymentData) => {

      message.success(`婵炴潙顑堥惁顖炲绩椤栨瑧甯涢柟瀛樺姇婵盯鏁嶆担绛嬪悅闁告娲栬ぐ? ${paymentData.out_trade_no}`);
      

      setQrVisible(false);
      setLoading(false);
      

      setPayResult({
        ...payResult,
        paymentSuccess: paymentData
      });
    };
    
    return () => {
      delete window.showPaymentSuccess;
    };
  }, [payResult]);

  const handleTestPayment = async () => {
    if (!amount || parseFloat(amount) <= 0) {
      message.error('閻犲洨鏌夌欢顓㈠礂閵夛附绠掗柡浣哥墢濞堟垿鏌岄幋锔绘澓');
      return;
    }

    setLoading(true);
    try {

      const orderNo = paymentAPI.generateOrderNo('TEST');
      

      const deviceType = paymentAPI.getDeviceType();
      const clientIP = await paymentAPI.getClientIP();
      

        payMethod,
        orderNo,
        productName,
        amount,
        deviceType,
        clientIP
      });
      

      const result = await paymentAPI.createOrder(
        payMethod,
        orderNo,
        productName,
        amount,
        JSON.stringify({
          test: true,
          timestamp: Date.now(),
          source: 'payment_test'
        }),
        `${window.location.origin}/api/payment/notify`,
        `${window.location.origin}/payment/success`,
        clientIP,
        deviceType
      );
      

      setPayResult(result);
      
      if (result.data && result.data.code === 1) {
        message.success('闁衡偓椤栨瑧甯涢悹浣靛灩瀹曠喖宕氬☉妯肩处闁瑰瓨鍔曟慨娑㈡晬?);
        

        if (deviceType === 'mobile' && result.data.payurl) {

          window.location.href = result.data.payurl;
        } else if (result.data.qrcode) {

          setPayUrl(result.data.qrcode);
          setQrVisible(true);
        } else if (result.data.payurl) {

          window.open(result.data.payurl, '_blank');
        }
      } else {
        message.error(result.data?.msg || '闁告帗绋戠紓鎾舵媼閵忕姴绀嬪鎯扮簿鐟?);
      }
    } catch (error) {

      message.error('闁衡偓椤栨瑧甯涙繛鏉戭儓閻︻垱寰勬潏顐バ? ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleTestHTTP = async () => {
    if (!amount || parseFloat(amount) <= 0) {
      message.error('閻犲洨鏌夌欢顓㈠礂閵夛附绠掗柡浣哥墢濞堟垿鏌岄幋锔绘澓');
      return;
    }

    setLoading(true);
    try {
      const orderNo = paymentAPI.generateOrderNo('HTTP_TEST');
      const deviceType = paymentAPI.getDeviceType();
      const clientIP = await paymentAPI.getClientIP();
      

        payMethod,
        orderNo,
        productName,
        amount,
        deviceType,
        clientIP
      });
      

      const result = await paymentAPI.createOrderHTTP(
        payMethod,
        orderNo,
        productName,
        amount,
        JSON.stringify({ test: true, http: true }),
        null,
        null,
        clientIP,
        deviceType
      );
      

      setPayResult(result);
      
      if (result.code === 1) {
        message.success('HTTP闁衡偓椤栨瑧甯涢悹浣靛灩瀹曠喖宕氬☉妯肩处闁瑰瓨鍔曟慨娑㈡晬?);
        
        if (result.qrcode) {
          setPayUrl(result.qrcode);
          setQrVisible(true);
        } else if (result.payurl) {
          window.open(result.payurl, '_blank');
        }
      } else {
        message.error(result.msg || 'HTTP闁告帗绋戠紓鎾舵媼閵忕姴绀嬪鎯扮簿鐟?);
      }
    } catch (error) {

      message.error('HTTP闁衡偓椤栨瑧甯涙繛鏉戭儓閻︻垱寰勬潏顐バ? ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24, maxWidth: 800, margin: '0 auto' }}>
      <Title level={2}>闁衡偓椤栨瑧甯涢柟鎭掑劚瑜版稑霉鐎ｎ厾妲稿銈囨暬濞?/Title>
      
      <Card title="闁衡偓椤栨瑧甯涢柛娆忓€归弳鐔兼煀瀹ュ洨鏋? style={{ marginBottom: 24 }}>
        <Space direction="vertical" size={16} style={{ width: '100%' }}>
          <div>
            <Text strong>闁哥喎妫楅幖褔宕ュ鍥嗙偤鏁?/Text>
            <Input 
              value={productName} 
              onChange={(e) => setProductName(e.target.value)}
              placeholder="閻犲洨鏌夌欢顓㈠礂閵夈儲娅岄柛婵呯閹洜绮?
              style={{ width: 200, marginLeft: 8 }}
            />
          </div>
          
          <div>
            <Text strong>闁衡偓椤栨瑧甯涢梺鍙夊灴椤ゅ倿鏁?/Text>
            <Input 
              value={amount} 
              onChange={(e) => setAmount(e.target.value)}
              placeholder="閻犲洨鏌夌欢顓㈠礂閵夆晛娅ㄥΛ?
              addonBefore="濡?
              style={{ width: 200, marginLeft: 8 }}
            />
          </div>
          
          <div>
            <Text strong>闁衡偓椤栨瑧甯涢柡鍌滄嚀缁憋繝鏁?/Text>
            <Radio.Group 
              value={payMethod} 
              onChange={(e) => setPayMethod(e.target.value)}
              style={{ marginLeft: 8 }}
            >
              {Object.entries(paymentAPI.paymentMethods).map(([key, method]) => (
                <Radio key={key} value={key}>
                  {method.icon} {method.name}
                </Radio>
              ))}
            </Radio.Group>
          </div>
          
          <div>
            <Text strong>閻犱焦鍎抽ˇ顒佺┍閳╁啩绱栭柨?/Text>
            <Text code style={{ marginLeft: 8 }}>
              閻犱焦鍎抽ˇ顒傜尵鐠囪尙鈧? {paymentAPI.getDeviceType()}
            </Text>
          </div>
        </Space>
      </Card>
      
      <Card title="婵炴潙顑堥惁顖炲箼瀹ュ嫮绋?>
        <Space size={16}>
          <Button 
            type="primary" 
            size="large"
            loading={loading}
            onClick={handleTestPayment}
          >
            WebSocket 闁衡偓椤栨瑧甯涙繛鏉戭儓閻?
          </Button>
          
          <Button 
            size="large"
            loading={loading}
            onClick={handleTestHTTP}
          >
            HTTP 闁衡偓椤栨瑧甯涙繛鏉戭儓閻?
          </Button>
        </Space>
      </Card>
      
      {payResult && (
        <Card title="闁衡偓椤栨瑧甯涚紓浣规尰閻? style={{ marginTop: 24 }}>
          <pre style={{ background: '#f5f5f5', padding: 16, borderRadius: 4, overflow: 'auto' }}>
            {JSON.stringify(payResult, null, 2)}
          </pre>
        </Card>
      )}
      
      <Modal
        title="闁规鍋嗛悥婊堝绩椤栨瑧甯?
        open={qrVisible}
        onCancel={() => setQrVisible(false)}
        footer={null}
        centered
      >
        <div style={{ textAlign: 'center', padding: '20px 0' }}>
          <QRCode value={payUrl} size={200} />
          <div style={{ marginTop: 16, color: '#666' }}>
            閻犲洩娓规繛鍥偨閳诲埦aymentAPI.paymentMethods[payMethod].name}闁规鍋嗛悥婊堝绩椤栨瑧甯?
          </div>
          <div style={{ marginTop: 8, fontSize: '12px', color: '#999' }}>
            闂佸弶鍨块·? 濡ょ磵amount}
          </div>
        </div>
      </Modal>
    </div>
  );
} 
