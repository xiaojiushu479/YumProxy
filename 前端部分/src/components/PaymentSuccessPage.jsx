import React, { useEffect, useState } from 'react';
import { Card, Result, Button, Descriptions, message } from 'antd';
import { CheckCircleOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { paymentAPI } from '../services/api';

export default function PaymentSuccessPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [orderInfo, setOrderInfo] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {

      setOrderInfo(location.state.paymentData);
    } else {

      const urlParams = new URLSearchParams(window.location.search);
      const outTradeNo = urlParams.get('out_trade_no');
        
      if (outTradeNo) {
        queryOrderInfo(outTradeNo);
      }
    }
  }, [location]);

  const queryOrderInfo = async (outTradeNo) => {
    setLoading(true);
    try {
      const username = localStorage.getItem('username');
      const userKey = localStorage.getItem('user_key');
      
      if (!username || !userKey) {
        message.error('閻犲洨鍏橀崳鎼佸棘閹殿喗顏㈢憸?);
        navigate('/login');
          return;
        }

      const bills = await paymentAPI.queryPayment(outTradeNo, 1, username, userKey);
        
      if (bills && bills.length > 0) {
        setOrderInfo(bills[0]);
        } else {
        message.error('闁哄牜浜濇竟姗€宕氶幏宀婂悅闁告娲戞穱濠囧箒?);
        }
      } catch (error) {

      message.error('闁哄被鍎撮妤冩媼閵忕姴绀嬪鎯扮簿鐟?);
      } finally {
        setLoading(false);
      }
    };

  const formatDateTime = (timestamp) => {
    if (!timestamp) return '闁哄牜浜為悡?;
    if (typeof timestamp === 'string' && timestamp.includes('-')) {
      return timestamp;
    }
    return new Date(parseInt(timestamp)).toLocaleString('zh-CN');
  };

  const getPayMethodName = (type) => {
    const methods = paymentAPI.paymentMethods;
    return methods[type]?.name || type;
  };

    return (
    <div style={{ padding: 24, maxWidth: 600, margin: '0 auto' }}>
          <Result
            icon={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
            status="success"
        title="闁衡偓椤栨瑧甯涢柟瀛樺姇婵?
        subTitle="闁规壆鍠曢梼鍧楀箖閵娧勭暠閻犳劦鍘洪幏閬嶆晬鐏炴儳浜堕柣銊ュ椤撳綊宕￠弴鐐插殥濠㈣泛瀚幃濠勨偓鐟版湰閸ㄦ岸鏁?
          />
      
          {orderInfo && (
        <Card title="閻犱降鍨瑰畷鐔烘嫚閿旇棄鍓? style={{ marginTop: 24 }}>
          <Descriptions column={1} bordered>
            <Descriptions.Item label="閻犱降鍨瑰畷鐔煎矗?>
              {orderInfo.out_trade_no}
            </Descriptions.Item>
            
            <Descriptions.Item label="闁哥喎妫楅幖褔宕ュ鍥?>
              {orderInfo.name || '闁哄啫鐖奸弳閬嶅礂閸涱厸鍋?}
            </Descriptions.Item>
            
            <Descriptions.Item label="闁衡偓椤栨瑧甯涢梺鍙夊灴椤?>
              濡ょ磵orderInfo.money || '闁哄牜浜為悡?}
            </Descriptions.Item>
            
            <Descriptions.Item label="闁衡偓椤栨瑧甯涢柡鍌滄嚀缁?>
              {getPayMethodName(orderInfo.type)}
            </Descriptions.Item>
            
            <Descriptions.Item label="闁衡偓椤栨瑧甯涢柡鍐ㄧ埣濡?>
              {formatDateTime(orderInfo.pay_time || orderInfo.timestamp)}
            </Descriptions.Item>
            
            {orderInfo.endtime && (
              <Descriptions.Item label="闁哄牆绉存慨鐔煎礆閻楀牊鍩傞柡鍐ㄧ埣濡?>
                {orderInfo.endtime}
              </Descriptions.Item>
            )}
            
            <Descriptions.Item label="閻犱降鍨瑰畷鐔兼偐閼哥鍋?>
              <span style={{ color: '#52c41a' }}>鐎瑰憡褰冮悾顒勫箣?/span>
            </Descriptions.Item>
          </Descriptions>
        </Card>
      )}
      
      <div style={{ textAlign: 'center', marginTop: 32 }}>
        <Button 
          type="primary" 
          size="large"
          onClick={() => navigate('/home')}
          style={{ marginRight: 16 }}
        >
          閺夆晜鏌ㄥú鏍純閺嶎厹鈧?
        </Button>
        
        <Button 
          size="large"
          onClick={() => navigate('/purchase')}
        >
          缂備綀鍛暰閻犳劦鍘洪幏?
        </Button>
      </div>
      
      {}
      <div style={{ textAlign: 'center', marginTop: 16, color: '#666', fontSize: '14px' }}>
        5缂佸甯掗幃妤呮嚊椤忓嫬袟閺夆晜鏌ㄥú鏍純閺嶎厹鈧?..
      </div>
      
      {}
      {React.useEffect(() => {
        const timer = setTimeout(() => {
          navigate('/home');
        }, 5000);
        
        return () => clearTimeout(timer);
      }, [])}
    </div>
  );
} 
