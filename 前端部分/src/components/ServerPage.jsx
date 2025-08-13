import React, { useState, useEffect, useRef } from 'react';
import { Card, Button, Statistic, Input, message } from 'antd';
import { CloudServerOutlined, SyncOutlined, UserOutlined } from '@ant-design/icons';
import { timestampAPI, whitelistAPI, userAPI } from '../services/api';

export default function ServerPage({ pid, username }) {

  const [time, setTime] = useState(0);
  const [ip] = useState('shpro.YumProxy.top');
  const [msg, setMsg] = useState('');
  const [mcId, setMcId] = useState('');
  const [timestampData, setTimestampData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [bindLoading, setBindLoading] = useState(false);
  const timerRef = useRef();
  const displayUsername = username || (pid === 0 ? 'admin' : 'user001');


    if (!displayUsername) return;
    
    try {
      setLoading(true);

      

      const userKey = localStorage.getItem('user_key');
      if (!userKey) {

        setTimestampData(null);
        return;
      }
      
      const response = await timestampAPI.query(displayUsername, userKey);

      

                       response.status === 'success' ||
                       (response.data && response.data.code === 200);
      
      if (isSuccess && response.data) {
        const rawData = response.data;
        

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
          expires_at: rawData.expiresAt,
          activated_at: rawData.activatedAt,
          code: rawData.code,
          message: rawData.message,
          raw_data: rawData
        };
        
        setTimestampData(processedData);

        

        if (processedData.is_active && processedData.expires_at) {
          try {
            const expiresDate = new Date(processedData.expires_at);
          const now = new Date();
            if (!isNaN(expiresDate.getTime())) {
              const remainingSeconds = Math.max(0, Math.floor((expiresDate - now) / 1000));
          setTime(remainingSeconds);

            }
          } catch (error) {

            setTime(0);
          }
        } else {
          setTime(0);
        }
      } else {

        setTimestampData(null);
        setTime(0);
      }
    } catch (error) {

      setTimestampData(null);
      setTime(0);
    } finally {
      setLoading(false);
    }
  };


  useEffect(() => {
    fetchTimestampData();
    

    const fetchBindedId = async () => {
      if (!displayUsername) return;
      try {

        

        const userKey = localStorage.getItem('user_key');
        const superKey = localStorage.getItem('super_key');
        
        if (!userKey) {

          return;
        }
        

        const response = await whitelistAPI.query(displayUsername, userKey, superKey);

        

        if (response.success === true || response.status === 'success') {
          let minecraftId = null;
          

          if (response.minecraft_id) {
            minecraftId = response.minecraft_id;

          }

          else if (response.minecraft_username) {
            minecraftId = response.minecraft_username;

          }

            const userBinding = response.data.find(item => 
              item.username === displayUsername && (item.minecraft_id || item.minecraft_username)
            );
            if (userBinding) {
              minecraftId = userBinding.minecraft_id || userBinding.minecraft_username;

            }
          }

            minecraftId = response.data.minecraft_id || response.data.minecraft_username;

          }
          

            setBindedId(minecraftId);

          } else if (response.is_bound === false) {

            setBindedId('');
          } else if (!minecraftId) {

            setBindedId('');
          } else {
            setBindedId(minecraftId);

          }
        } else {

          setBindedId('');
        }
      } catch (error) {

      }
    };
    fetchBindedId();
  }, [displayUsername]);

  useEffect(() => {
    if (running && time > 0) {
      timerRef.current = setInterval(() => {
        setTime(t => (t > 0 ? t - 1 : 0));
      }, 1000);
    } else {
      clearInterval(timerRef.current);
    }
    return () => clearInterval(timerRef.current);
  }, [running, time]);

  const formatTime = (t) => {
    const h = Math.floor(t / 3600);
    const m = Math.floor((t % 3600) / 60);
    const s = t % 60;
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const handleBind = async () => {
    if (!mcId.trim()) {
      message.error('閻犲洨鏌夌欢顓㈠礂椤ф悆necraft ID');
      return;
    }
    
    try {
      setBindLoading(true);

      

      const userKey = localStorage.getItem('user_key');
      if (!userKey) {
        message.error('闁活潿鍔嶉崺娑㈠嫉椤忓棙顏㈢憸鐗堟礃閸ㄣ劎绱撻崫鍕瘜閻犱降鍊涢惁澶嬬┍閳╁啩绱?);
        return;
      }
      

      const validateResponse = await whitelistAPI.validate(mcId.trim(), userKey);

      
      if (validateResponse.success === true) {
        if (validateResponse.exists === true) {


          return;
        } else {


        }
      } else {

      }
      


      const addResponse = await whitelistAPI.add(displayUsername, mcId.trim(), userKey);

      
      if (addResponse.success === true) {
        const newBindedId = mcId.trim();
        setBindedId(newBindedId);
      setMcId('');
      message.success('缂備焦鍨甸悾楣冨箣閹邦剙顫?);


      } else {
        message.error(addResponse.message || '缂備焦鍨甸悾鐐緞鏉堫偉袝');

      }
    } catch (error) {

      message.error('缂備焦鍨甸悾鐐緞鏉堫偉袝: ' + (error.message || '閻犲洭顥撻埣銏ゅ触鎼淬劌娅㈤悹?));
    } finally {
      setBindLoading(false);
    }
  };

  const handleSync = async () => {
    try {

        bindedId, 
        displayUsername,
        bindedIdType: typeof bindedId,
        bindedIdLength: bindedId?.length 
      });
      
      if (!bindedId || bindedId.trim() === '') {
        message.error('閻犲洤鍢查崢娑氱磼閹存繄鏆癕inecraft ID');

        return;
      }
      

      setMsg('婵繐绲藉﹢顏堝触鐏炵虎鍔勯柣褑妫勯幃鏇㈠础?..');
      

      const userKey = localStorage.getItem('user_key');
      if (!userKey) {
        message.error('闁活潿鍔嶉崺娑㈠嫉椤忓棙顏㈢憸鐗堟礃閸ㄣ劎绱撻崫鍕瘜閻犱降鍊涢惁澶嬬┍閳╁啩绱?);
        setMsg('闁告艾鏈鐐村緞鏉堫偉袝闁挎稒姘ㄥ杈╀焊閹达綆鍚囬悹?);
        return;
      }
      

      const response = await whitelistAPI.add(displayUsername, bindedId, userKey);

      
      if (response.success === true) {
      setMsg('闁谎嗘閹洟宕￠弴鐐插殥闁告艾鏈?);
      message.success('闁谎嗘閹洟宕￠弴鐐插殥闁告艾鏈?);

      } else {
        const errorMsg = response.message || '闁告艾鏈鐐烘儌閽樺鍊抽柛妤佹礀閵囨垹鎷?;
        setMsg(`闁告艾鏈鐐村緞鏉堫偉袝: ${errorMsg}`);
        message.error(errorMsg);

      }
    } catch (error) {

      setMsg('闁告艾鏈鐐村緞鏉堫偉袝');
      message.error(error.message || '闁告艾鏈鐐烘儌閽樺鍊抽柛妤佹礀閵囨垹鎷?);
    }
  };


  const handleUnbind = async () => {
    if (!bindedId) {
      message.error('婵炲备鍓濆﹢渚€宕ｉ婢帞绱掗幋鐘崇暠ID');
      return;
    }

    try {
      setBindLoading(true);



      const userKey = localStorage.getItem('user_key');
      if (!userKey) {
        message.error('闁活潿鍔嶉崺娑㈠嫉椤忓棙顏㈢憸鐗堟礃閸ㄣ劎绱撻崫鍕瘜閻犱降鍊涢惁澶嬬┍閳╁啩绱?);
        return;
      }


        displayUsername,
        userKey: userKey ? userKey.substring(0, 10) + '...' : 'null',
        userKeyLength: userKey ? userKey.length : 0
      });




      if (response.success === true) {
        const oldBindedId = bindedId;
        setBindedId('');
        message.success('閻熸瑱绲跨划锕傚箣閹邦剙顫?);


      } else {
        message.error(response.message || '閻熸瑱绲跨划锔藉緞鏉堫偉袝');

      }
    } catch (error) {

      message.error('閻熸瑱绲跨划锔藉緞鏉堫偉袝: ' + (error.message || '閻犲洭顥撻埣銏ゅ触鎼淬劌娅㈤悹?));
    } finally {
      setBindLoading(false);
    }
  };


  const handleRefresh = async () => {

    setMsg('婵繐绲藉﹢顏堝礆闁垮鐓€闁轰胶澧楀畵?..');
    
    try {
      await Promise.all([
        fetchTimestampData(),

        (async () => {
          if (!displayUsername) return;
          try {
            const userKey = localStorage.getItem('user_key');
            const superKey = localStorage.getItem('super_key');
            
            if (!userKey) return;
            
            const response = await whitelistAPI.query(displayUsername, userKey, superKey);

            

            if (response.success === true || response.status === 'success') {
              let minecraftId = null;
              

              if (response.minecraft_id) {
                minecraftId = response.minecraft_id;
              } else if (response.minecraft_username) {
                minecraftId = response.minecraft_username;
              } else if (response.data && Array.isArray(response.data)) {
                const userBinding = response.data.find(item => 
                  item.username === displayUsername && (item.minecraft_id || item.minecraft_username)
                );
                if (userBinding) {
                  minecraftId = userBinding.minecraft_id || userBinding.minecraft_username;
                }
              } else if (response.data && (response.data.minecraft_id || response.data.minecraft_username)) {
                minecraftId = response.data.minecraft_id || response.data.minecraft_username;
              }
              

                setBindedId(minecraftId);

              } else if (response.is_bound === false) {

                setBindedId('');
              } else if (!minecraftId) {

                setBindedId('');
              } else {
                setBindedId(minecraftId);

              }
            } else {
              setBindedId('');

            }
          } catch (error) {

          }
        })()
      ]);
      
      setMsg('闁轰胶澧楀畵浣割啅閹绘帒鐓曢柡?);
      message.success('闁轰胶澧楀畵浣割啅閹绘帒鐓曢柡?);
      

      setTimeout(() => setMsg(''), 2000);
    } catch (error) {

      setMsg('闁告帡鏀遍弻濠冨緞鏉堫偉袝');
      message.error('闁告帡鏀遍弻濠冨緞鏉堫偉袝');
    }
  };


  let content;
  if (loading) {
    content = <div style={{ color: '#999' }}>闁告梻濮惧ù鍥ㄧ▔?..</div>;
  } else if (!timestampData || !timestampData.is_active) {
    content = <div style={{ color: '#ff4d4f', fontWeight: 500 }}>闁哄牜浜滅槐鎴︽焻濮橆厽顦ч梻鈧崠锛勭閻犲洤鍢叉晶鐘差嚗閳ь剛鎷归婵囧濡炪倗鏁诲鏉款嚕閳ь剟鏌呭鍓х＜</div>;
  } else {
    content = (
      <>
        <Statistic 
          title="闁告挴鏅欑紞鎴﹀籍閸洘锛?(閻忓繐绻戝?闁告帒妫濋幐?缂?" 
          value={formatTime(time)} 
          valueStyle={{ color: '#1677ff', fontWeight: 700 }} 
          style={{ marginBottom: 24 }} 
        />
        {timestampData.expires_at && (
          <div style={{ 
            marginTop: 16, 
            padding: 12, 
            background: 'rgba(22, 119, 255, 0.1)', 
            borderRadius: 8,
            fontSize: 14,
            color: '#666'
          }}>
            <div style={{ marginBottom: 4 }}>
              <strong>闁告帞澧楀﹢锟犲籍閸洘锛?</strong> {timestampData.expires_at}
            </div>
            {timestampData.activated_at && (
              <div>
                <strong>婵犵鍋撴繛鑼帛濡炲倿姊?</strong> {timestampData.activated_at}
              </div>
            )}
          </div>
        )}
      </>
    );
  }

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: '0 24px' }}>
      <Card
        style={{
          background: 'var(--card-bg)',
          borderRadius: 'var(--border-radius)',
          padding: 24,
          boxShadow: '0 2px 12px rgba(0,0,0,0.04)',
        }}
      >
        <div style={{ 
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: 24
        }}>
          <h2 style={{ 
            display: 'flex', 
            alignItems: 'center', 
            gap: 12, 
            fontWeight: 600, 
            fontSize: 24,
            color: 'var(--text-color)',
            margin: 0
          }}>
            <CloudServerOutlined style={{ color: 'var(--primary-color)', fontSize: 28 }} />
            闁哄牆绉存慨鐔煎闯閵娧冃﹂柟?          </h2>
          <div style={{ 
            background: 'rgba(22, 119, 255, 0.1)',
            color: 'var(--primary-color)',
            padding: '6px 12px',
            borderRadius: 16,
            fontWeight: 500
          }}>
            闁革负鍔庨崵?<span style={{ color: '#52c41a' }}>闁?/span>
          </div>
        </div>
        <div style={{ 
          display: 'flex',
          flexWrap: 'nowrap',
          overflowX: 'auto',
          gap: 24,
          marginBottom: 32,
          paddingBottom: 16
        }}>
          <div style={{
            background: 'rgba(0,0,0,0.02)',
            padding: 16,
            borderRadius: 8,
            border: '1px solid rgba(0,0,0,0.04)',
            minWidth: 200,
            flex: '0 0 auto'
          }}>
            <div style={{ color: 'var(--text-color)', opacity: 0.8 }}>闁哄牆绉存慨鐔煎闯閳湢</div>
            <div style={{ 
              fontSize: 18,
              fontWeight: 600,
              marginTop: 8,
              color: 'var(--primary-color)'
            }}>{ip}</div>
          </div>
          <div style={{
            background: 'rgba(0,0,0,0.02)',
            padding: 16,
            borderRadius: 8,
            border: '1px solid rgba(0,0,0,0.04)'
          }}>
            <div style={{ color: 'var(--text-color)', opacity: 0.8 }}>闁革负鍔庨崵搴㈢閻戞ɑ娈?/div>
            <div style={{ 
              fontSize: 18,
              fontWeight: 600,
              marginTop: 8,
              color: 'var(--primary-color)'
            }}>12/50</div>
          </div>
          <div style={{
            background: 'rgba(0,0,0,0.02)',
            padding: 16,
            borderRadius: 8,
            border: '1px solid rgba(0,0,0,0.04)'
          }}>
            <div style={{ color: 'var(--text-color)', opacity: 0.8 }}>闁哄牆绉存慨鐔煎闯閵娧冾暭闁?/div>
            <div style={{ 
              fontSize: 18,
              fontWeight: 600,
              marginTop: 8,
              color: 'var(--primary-color)'
            }}>Hypixel</div>
          </div>
        </div>
        <div style={{ 
          background: 'rgba(0,0,0,0.02)',
          padding: 24,
          borderRadius: 8,
          marginBottom: 24,
          border: '1px solid rgba(0,0,0,0.04)'
        }}>
          {content}
          <div style={{ 
            display: 'flex', 
            gap: 12, 
            justifyContent: 'center', 
            marginTop: 24,
            flexWrap: 'wrap' 
          }}>
            <Button 
              type="default"
              icon={<SyncOutlined />}
              onClick={handleSync}
              style={{ 
                borderRadius: 'var(--border-radius)',
                minWidth: 100,
                height: 40
              }}
            >闁告艾鏈鐐烘儌閽樺鍊抽柛?/Button>
            <Button 
              type="primary"
              icon={<SyncOutlined />}
              onClick={handleRefresh}
              style={{ 
                borderRadius: 'var(--border-radius)',
                minWidth: 100,
                height: 40
              }}
            >闁告帡鏀遍弻濠囧极閻楀牆绁?/Button>
          </div>
        </div>
        <div style={{ 
          background: 'rgba(0,0,0,0.02)',
          padding: 24,
          borderRadius: 8,
          marginTop: 24,
          border: '1px solid rgba(0,0,0,0.04)'
        }}>
          <div style={{ 
            display: 'flex',
            alignItems: 'center',
            gap: 8,
            marginBottom: 16,
            color: 'var(--text-color)'
          }}>
            <UserOutlined style={{ fontSize: 18 }} />
            <div style={{ fontWeight: 500, fontSize: 16 }}>Minecraft ID缂備焦鍨甸悾?/div>
            {}
            <div style={{ 
              fontSize: 12, 
              color: '#999', 
              marginLeft: 'auto',
              fontFamily: 'monospace' 
            }}>
              闁绘鍩栭埀? {bindedId ? `鐎规瓕灏欑划锔锯偓?${bindedId})` : '闁哄牜浜炵划锔锯偓?}
            </div>
          </div>
          {bindedId ? (
            <div>
            <div style={{ 
              background: 'rgba(0,0,0,0.04)',
              padding: 12,
              borderRadius: 8,
                textAlign: 'center',
                marginBottom: 12
            }}>
              鐎规瓕灏欑划锔锯偓瑙勵儣D: <b style={{ color: 'var(--primary-color)' }}>{bindedId}</b>
              </div>
              <div style={{ display: 'flex', justifyContent: 'center' }}>
                <Button 
                  type="default"
                  danger
                  onClick={handleUnbind}
                  loading={bindLoading}
                  disabled={bindLoading}
                  style={{ 
                    borderRadius: 'var(--border-radius)',
                    minWidth: 100
                  }}
                >
                  {bindLoading ? '閻熸瑱绲跨划锔界▔?..' : '閻熸瑱绲跨划顨疍'}
                </Button>
              </div>
            </div>
          ) : (
            <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
              <Input
                placeholder="閺夊牊鎸搁崣鍡樻媴閻樺灚鐣盡inecraft ID"
                value={mcId}
                onChange={e => setMcId(e.target.value)}
                style={{ 
                  flex: 1,
                  minWidth: 200,
                  borderRadius: 'var(--border-radius)',
                  maxWidth: 300
                }}
                onPressEnter={handleBind}
              />
              <Button 
                type="primary" 
                onClick={handleBind} 
                style={{ 
                  borderRadius: 'var(--border-radius)',
                  minWidth: 100
                }}
                loading={bindLoading}
                disabled={bindLoading}
              >
                {bindLoading ? '缂備焦鍨甸悾鐐▔?..' : '缂備焦鍨甸悾?}
              </Button>
            </div>
          )}
        </div>
        {msg && (
          <div style={{ 
            color: 'var(--primary-color)',
            fontSize: 15,
            marginTop: 16,
            textAlign: 'center'
          }}>
            {msg}
          </div>
        )}
        <style>{`
          @media (max-width: 600px) {
            .server-card-ani {
              min-width: 0 !important;
              max-width: 100vw !important;
              margin: 0 4vw;
            }
          }
          @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(40px); }
            to { opacity: 1; transform: none; }
          }
        `}</style>
      </Card>
    </div>
  );
}
