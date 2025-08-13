import React, { useEffect, useState } from 'react';
import { message } from 'antd';
import { timestampAPI, whitelistAPI } from '../services/api';

export default function HomePage({ pid, username }) {
  const [timestampData, setTimestampData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [bindedId, setBindedId] = useState('');


    if (!username) return;
    
    const userKey = localStorage.getItem('user_key');
    if (!userKey) return;
    
    try {

      

      
      const isSuccess = timestampResponse.success === true || 
                       timestampResponse.status === 'success' ||
                       (timestampResponse.data && timestampResponse.data.code === 200);
      
      if (isSuccess) {

        

        

        const foundRecord = rawData.data === 'Found' || rawData.data === 'found';
        

        let isExpired = false;
        if (foundRecord && rawData.expiresAt) {
          try {
            const expiresDate = new Date(rawData.expiresAt);
            const now = new Date();
            isExpired = expiresDate <= now;
          } catch (error) {

            isExpired = true;
          }
        }
        
        const processedData = {
          is_active: foundRecord && !isExpired,
          expires_at: rawData.expiresAt || rawData.activatedAt || null,
          activated_at: rawData.activatedAt,
          code: rawData.code,
          message: rawData.message,
          times: rawData.times,
          is_expired: isExpired,
          found_record: foundRecord,
          raw_data: rawData
        };
        
        setTimestampData(processedData);
      }
    } catch (error) {

    }
  };


  useEffect(() => {

      window.showPaymentSuccess = (paymentData) => {

        message.success(`闁衡偓椤栨瑧甯涢柟瀛樺姇婵盯鏁嶆担绛嬪悅闁告娲栬ぐ? ${paymentData.out_trade_no}`);
        

        setTimeout(() => {
          refreshUserData();
        }, 1000);
      };
    }
    
    return () => {

    };
  }, [username]);



  useEffect(() => {
    let isActive = true;
    
    const fetchAllData = async () => {
      if (!username) return;
      
      const userKey = localStorage.getItem('user_key');
      if (!userKey) {
        message.error('閻犲洨鍏橀崳鎼佸棘閹殿喗顏㈢憸?);
        return;
      }
      

      setLoading(true);
      
      try {

        const timestampResponse = await timestampAPI.query(username, userKey);
        

        

                                 timestampResponse.status === 'success' ||
                                 (timestampResponse.data && timestampResponse.data.code === 200);
        
        if (isActive && isTimestampSuccess) {

          

          

            expiresAt: rawData.expiresAt,
            activatedAt: rawData.activatedAt,
            expiresAtType: typeof rawData.expiresAt,
            activatedAtType: typeof rawData.activatedAt,
            dataField: rawData.data,
            dataType: typeof rawData.data,
            isFoundMatch: rawData.data === 'Found',
            isfoundMatch: rawData.data === 'found'
          });
          

          const foundRecord = rawData.data === 'Found' || rawData.data === 'found';
          

          let isExpired = false;
          if (foundRecord && rawData.expiresAt) {
            try {
              const expiresDate = new Date(rawData.expiresAt);
              const now = new Date();
              isExpired = expiresDate <= now;
            } catch (error) {

              isExpired = true;
            }
          }
          
          const processedData = {
            is_active: foundRecord && !isExpired,
            expires_at: rawData.expiresAt || rawData.activatedAt || null,
            activated_at: rawData.activatedAt,
            code: rawData.code,
            message: rawData.message,
            times: rawData.times,
            is_expired: isExpired,
            found_record: foundRecord,
            raw_data: rawData
          };
          

          

            rawData: rawData.data,
            foundRecord: processedData.found_record,
            isExpired: processedData.is_expired,
            isActive: processedData.is_active,
            expiresAt: rawData.expiresAt,
            currentTime: new Date().toISOString(),
            闁告帇鍊栭弻鍥焻閺勫繒甯? 'foundRecord && !isExpired'
          });
          

          if (processedData.expires_at) {
            try {
              const testDate = new Date(processedData.expires_at);

                original: processedData.expires_at,
                parsed: testDate,
                isValid: !isNaN(testDate.getTime()),
                timestamp: testDate.getTime()
              });
            } catch (error) {

            }
          }
          setTimestampData(processedData);
        } else if (isActive) {

          message.error('闁哄啫鐖煎Λ鍧楀箣鐎圭姴绠柛娆愮墪閵囨垹鎷归妷顖滅閻犲洨鍏橀崳鍝ユ嫚?);
        }
        


        const whitelistResponse = await whitelistAPI.query(username, userKey);
        

        
        if (isActive) {

          const isSuccess = whitelistResponse.success === true || whitelistResponse.status === 'success';

            status: whitelistResponse.status, 
            success: whitelistResponse.success, 
            is_bound: whitelistResponse.is_bound,
            isSuccess 
          });
          
          if (isSuccess) {
            let minecraftId = null;
            

            if (whitelistResponse.minecraft_id) {
              minecraftId = whitelistResponse.minecraft_id;

            } else if (whitelistResponse.minecraft_username) {
              minecraftId = whitelistResponse.minecraft_username;

            } else if (whitelistResponse.data && Array.isArray(whitelistResponse.data)) {
              const userBinding = whitelistResponse.data.find(item => 
                item.username === username && (item.minecraft_id || item.minecraft_username)
              );
              if (userBinding) {
                minecraftId = userBinding.minecraft_id || userBinding.minecraft_username;

              }
            } else if (whitelistResponse.data && (whitelistResponse.data.minecraft_id || whitelistResponse.data.minecraft_username)) {
              minecraftId = whitelistResponse.data.minecraft_id || whitelistResponse.data.minecraft_username;

            }
            


              setBindedId(minecraftId);
            } else if (whitelistResponse.is_bound === false) {

              setBindedId('');
            } else if (!minecraftId) {

              setBindedId('');
            } else {

              setBindedId(minecraftId);
            }
          } else {

            setBindedId('');
          }
        }
        
      } catch (error) {
        if (isActive) {

          setBindedId('');
        }
      } finally {
        if (isActive) {
          setLoading(false);

        }
      }
    };
    
    fetchAllData();
    
    return () => {
      isActive = false;
    };
  }, [username]);


    const y = date.getFullYear();
    const m = (date.getMonth() + 1).toString().padStart(2, '0');
    const d = date.getDate().toString().padStart(2, '0');
    const h = date.getHours().toString().padStart(2, '0');
    const min = date.getMinutes().toString().padStart(2, '0');
    const s = date.getSeconds().toString().padStart(2, '0');
    return `${y}-${m}-${d} ${h}:${min}:${s}`;
  }


  function formatTime(t) {
    const h = Math.floor(t / 3600);
    const m = Math.floor((t % 3600) / 60);
    const s = t % 60;
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  }


  let content;
  if (loading) {
    content = <div style={{ color: '#999' }}>闁告梻濮惧ù鍥ㄧ▔?..</div>;
  } else if (!timestampData || !timestampData.is_active) {
    content = <div style={{ color: '#ff4d4f', fontWeight: 500 }}>闁哄牜浜滅槐鎴︽焻濮橆厽顦ч梻鈧崠锛勭閻犲洤鍢叉晶鐘差嚗閳ь剛鎷归婵囧濡炪倗鏁诲鏉款嚕閳ь剟鏌呭鍓х＜</div>;
  } else {
    let expiresAtFormatted = '闁?;
    if (timestampData.expires_at) {
      try {
        const date = new Date(timestampData.expires_at);
        if (isNaN(date.getTime())) {

          expiresAtFormatted = '闁哄啫鐖煎Λ鍧楀冀閻撳海纭€闂佹寧鐟ㄩ?;
        } else {
      expiresAtFormatted = formatDate(date);
        }
      } catch (error) {

        expiresAtFormatted = '閻熸瑱绲鹃悗鑺ュ緞鏉堫偉袝';
      }
    }
    content = (
      <>
        <div style={{ marginBottom: 8 }}>闁绘鍩栭埀顑跨筏缁?b style={{ color: timestampData.is_active ? '#52c41a' : '#ff4d4f' }}>{timestampData.is_active ? '鐎圭寮剁缓鍝劽? : '闁哄牜浜濈缓鍝劽?}</b></div>
        {timestampData.activated_at && (
          <div style={{ marginBottom: 8 }}>婵犵鍋撴繛鑼帛濡炲倿姊绘潏鍓х獥<b>{timestampData.activated_at}</b></div>
        )}
        <div style={{ marginBottom: 8 }}>闁告帞澧楀﹢锟犲籍閸洘锛熼柨?b>{expiresAtFormatted}</b></div>
        <div style={{ marginBottom: 8 }}>闁告挴鏅欑紞鎴﹀籍閸洘姣愰柨?b>
          {(() => {
            if (!timestampData.expires_at || timestampData.expires_at === timestampData.activated_at) {
              return '閻犲洨鏌夋禒鍫㈠寲閼姐値鍚€闁荤偛妫楅幉鎶藉蓟閵壯勭畽';
            }
            try {
              const expiresDate = new Date(timestampData.expires_at);
              const now = new Date();
              if (isNaN(expiresDate.getTime())) {

                return '闁哄啫鐖煎Λ鍧楀冀閻撳海纭€闂佹寧鐟ㄩ?;
              }
              const remainingSeconds = Math.max(0, Math.floor((expiresDate - now) / 1000));
              return formatTime(remainingSeconds);
            } catch (error) {

              return '閻犱緤绱曢悾缁樺緞鏉堫偉袝';
            }
          })()}
        </b></div>
      </>
    );
  }


    if (!timestampData || !timestampData.is_active) return '闁哄牜浜ｉ崰妯荤▕?;
    

    if (timestampData.expires_at && timestampData.activated_at) {
      try {
        const expiresDate = new Date(timestampData.expires_at);
        const activatedDate = new Date(timestampData.activated_at);
        
        if (isNaN(expiresDate.getTime()) || isNaN(activatedDate.getTime())) {

            expires_at: timestampData.expires_at,
            activated_at: timestampData.activated_at
          });
          return '闁哄啫鐖煎Λ鍧楁煥濞嗘帩鍤?;
        }
        
        totalHours = Math.ceil((expiresDate - activatedDate) / (1000 * 3600));

          activated_at: timestampData.activated_at,
          expires_at: timestampData.expires_at,
          totalHours: totalHours
        });
        
      } catch (error) {

        return '閻犱緤绱曢悾濠氭煥濞嗘帩鍤?;
      }
    } else {
      return '闁轰胶澧楀畵浣圭▔瀹ュ懐鏆氶柡?;
    }
    

    if (totalHours <= 72) return '濞戞挸顦妵澶愬础?;  
    if (totalHours <= 168) return '闁告稏鍔屽畷?;
    if (totalHours <= 720) return '闁哄牆鐗嗗畷?;
    return '闂傗偓閹稿孩鍩傞柛?;
  };



  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.5fr', gap: 24 }}>
      {}
      <div style={{ display: 'grid', gap: 24 }}>
        {}
        <div style={{ background: 'var(--card-bg)', borderRadius: 'var(--border-radius)', padding: 24 }}>
          <h2 style={{ color: 'var(--primary-color)', marginBottom: 16 }}>闁活潿鍔嶉崺娑欑┍閳╁啩绱?/h2>
          <div style={{ marginBottom: 8 }}>闁活潿鍔嶉崺娑㈠触瀹ュ繒绐?b>{username}</b></div>
          <div style={{ marginBottom: 8 }}>閻犱降鍨藉Σ鍕尵鐠囪尙鈧兘鏁?b>{getSubscriptionType()}</b></div>
          {content}
        </div>

        {}
        <div style={{ background: 'var(--card-bg)', borderRadius: 'var(--border-radius)', padding: 24 }}>
          <h2 style={{ color: 'var(--primary-color)', marginBottom: 16 }}>Minecraft ID</h2>
          <div style={{ 
            padding: '16px 20px',
            background: '#ffffff',
                borderRadius: 'var(--border-radius)',
            border: '1px solid #e8e8e8',
            textAlign: 'left'
          }}>
            {bindedId ? (
              <>
                <div style={{ 
                  fontSize: '16px', 
                  fontWeight: 500, 
                  color: '#52c41a',
                  marginBottom: 12,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}>
                  闁?鐎规瓕灏欑划锔锯偓?                </div>
                <div style={{ 
                  fontSize: '18px', 
                  color: 'var(--primary-color)',
                  fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial, sans-serif',
                  fontWeight: 600,
                  letterSpacing: '0.5px'
                }}>
                  {bindedId}
                </div>
              </>
            ) : (
              <>
                <div style={{ 
                  fontSize: '16px', 
                  fontWeight: 500, 
                  color: '#999',
                  marginBottom: 12,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}>
                  闁?闁哄牜浜炵划锔锯偓?                </div>
                <div style={{ 
                  fontSize: '14px', 
                  color: '#666'
                }}>
                  閻犲洤鍢叉晶鐘差嚗閳ь剟寮靛鍛潳闁革絻鍔戦妴澶愭閵忥絿绠婚悶娑樼灱缁妇鈧?          </div>
              </>
            )}
          </div>
        </div>
      </div>

      {}
      <div style={{ background: 'var(--card-bg)', borderRadius: 'var(--border-radius)', padding: 24 }}>
        <h2 style={{ color: 'var(--primary-color)', marginBottom: 16 }}>闁稿浚鍓欓幉锟犲冀?/h2>
        <div style={{ 
          minHeight: 300,
          padding: 16,
          background: 'rgba(0,0,0,0.02)',
          borderRadius: 8,
          border: '1px dashed rgba(0,0,0,0.1)'
        }}>
          <div style={{ color: '#999' }}>闁哄棗鍊瑰Λ銈夊礂椤掆偓閹?/div>
        </div>
        
        
      </div>
    </div>
  );
}
