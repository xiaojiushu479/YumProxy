import { useState } from 'react';
import { Card, Input, Button, Tag, message } from 'antd';
import { UserSwitchOutlined } from '@ant-design/icons';

export default function IdPage() {
  const [mcid, setMcid] = useState('Steve');
  const [input, setInput] = useState('');
  const [msg, setMsg] = useState('');
  const handleBind = () => {
    if (input) {
      setMcid(input);
      setMsg('缂備焦鍨甸悾楣冨箣閹邦剙顫犻柨?);
      message.success('缂備焦鍨甸悾楣冨箣閹邦剙顫犻柨?);
    } else {
      setMsg('閻犲洨鏌夌欢顓㈠礂椤у粚');
      message.error('閻犲洨鏌夌欢顓㈠礂椤у粚');
    }
  };
  return (
    <Card style={{ maxWidth: 400, margin: '0 auto', marginTop: 48, borderRadius: 16, textAlign: 'center' }}>
      <h2><UserSwitchOutlined /> Minecraft ID</h2>
      <div style={{ marginBottom: 16 }}>
        鐟滅増鎸告晶鐘电磼閹存繄鏆癐D闁挎稒顒竚cid ? <Tag color="blue">{mcid}</Tag> : <Tag color="red">闁哄牜浜炵划锔锯偓?/Tag>}
      </div>
      <Input
        value={input}
        onChange={e => setInput(e.target.value)}
        placeholder="閺夊牊鎸搁崣鍡涘棘閻ф€?
        style={{ marginBottom: 16 }}
        size="large"
      />
      <Button type="primary" block size="large" onClick={handleBind}>
        闁瑰箍鍨荤划?
      </Button>
      {msg && <div style={{ marginTop: 18, color: msg.includes('闁瑰瓨鍔曟慨?) ? '#52c41a' : '#ff4d4f' }}>{msg}</div>}
    </Card>
  );
}
