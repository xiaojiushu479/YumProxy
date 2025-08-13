import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Form, 
  Input, 
  Button, 
  Table, 
  message, 
  Space, 
  Typography, 
  Tabs, 
  Modal, 
  Tag,
  InputNumber,
  Switch,
  Tooltip,
  Popconfirm
} from 'antd';
import { 
  BanOutlined, 
  CheckCircleOutlined, 
  HistoryOutlined, 
  DeleteOutlined,
  ReloadOutlined,
  SearchOutlined
} from '@ant-design/icons';
import { userBanAPI } from '../services/api';

const { Title, Text } = Typography;
const { TabPane } = Tabs;
const { TextArea } = Input;

const UserBanManager = () => {

  const [loading, setLoading] = useState(false);
  const [bannedUsers, setBannedUsers] = useState([]);
  const [banLogs, setBanLogs] = useState([]);
  const [superKey, setSuperKey] = useState('');
  

  const [banForm] = Form.useForm();
  const [checkForm] = Form.useForm();
  

  const [banModalVisible, setBanModalVisible] = useState(false);
  const [checkModalVisible, setCheckModalVisible] = useState(false);
  const [checkResult, setCheckResult] = useState(null);


  const loadBannedUsers = async () => {
    if (!superKey.trim()) {
      message.warning('閻犲洤鍢查崢娑欐綇閹惧啿寮崇紒鐙呯磿閹﹪宕ㄥΟ铏规闂?);
      return;
    }

    try {
      setLoading(true);

      
      const response = await userBanAPI.listBanned(superKey.trim());

      

      const responseData = response.data || response;
      const success = responseData.success || response.status === 'success';
      
      if (success) {
        const users = responseData.banned_users || [];
        setBannedUsers(users);
        message.success(`闁告梻濮惧ù鍥箣閹邦剙顫犻柨娑樿嫰閸?${users.length} 濞戞搩浜滈惃婵堢矉娴ｇ儤鏆忛柟瀵革公);
      } else {
        message.error('闁告梻濮惧ù鍥ㄥ緞鏉堫偉袝: ' + (responseData.message || '闁哄牜浜為悡锟犳煥濞嗘帩鍤?));
      }
    } catch (error) {

      message.error('闁告梻濮惧ù鍥ㄥ緞鏉堫偉袝: ' + error.message);
    } finally {
      setLoading(false);
    }
  };


  const loadBanLogs = async (targetUsername = null, limit = 50) => {
    if (!superKey.trim()) {
      message.warning('閻犲洤鍢查崢娑欐綇閹惧啿寮崇紒鐙呯磿閹﹪宕ㄥΟ铏规闂?);
      return;
    }

    try {
      setLoading(true);

      
      const response = await userBanAPI.getLogs(superKey.trim(), targetUsername, limit);

      

      const responseData = response.data || response;
      const success = responseData.success || response.status === 'success';
      
      if (success) {
        const logs = responseData.logs || [];
        setBanLogs(logs);
        message.success(`闁告梻濮惧ù鍥箣閹邦剙顫犻柨娑樿嫰閸?${logs.length} 闁哄鍓濆Λ鈺勭疀濮?;
      } else {
        message.error('闁告梻濮惧ù鍥ㄥ緞鏉堫偉袝: ' + (responseData.message || '闁哄牜浜為悡锟犳煥濞嗘帩鍤?));
      }
    } catch (error) {

      message.error('闁告梻濮惧ù鍥ㄥ緞鏉堫偉袝: ' + error.message);
    } finally {
      setLoading(false);
    }
  };


  const handleBanUser = async (values) => {
    if (!superKey.trim()) {
      message.warning('閻犲洤鍢查崢娑欐綇閹惧啿寮崇紒鐙呯磿閹﹪宕ㄥΟ铏规闂?);
      return;
    }

    try {
      setLoading(true);
      const { username, reason, duration, isPermanent } = values;
      const finalDuration = isPermanent ? null : duration;
      

      
      const response = await userBanAPI.ban(
        username.trim(),
        reason || '缂佺媴绱曢幃濠囧川濡櫣娈辩紒?,
        finalDuration,
        superKey.trim()
      );
      

      

      const responseData = response.data || response;
      const success = responseData.success || response.status === 'success';
      
      if (success) {
        message.success('閻忓繋鑳堕々锕傚箣閹邦剙顫?');
        setBanModalVisible(false);
        banForm.resetFields();
        

        await Promise.all([
          loadBannedUsers(),
          loadBanLogs()
        ]);
      } else {
        message.error('閻忓繋鑳堕々锔藉緞鏉堫偉袝: ' + (responseData.message || '闁哄牜浜為悡锟犳煥濞嗘帩鍤?));
      }
    } catch (error) {

      message.error('閻忓繋鑳堕々锔藉緞鏉堫偉袝: ' + error.message);
    } finally {
      setLoading(false);
    }
  };


  const handleUnbanUser = async (username) => {
    if (!superKey.trim()) {
      message.warning('閻犲洤鍢查崢娑欐綇閹惧啿寮崇紒鐙呯磿閹﹪宕ㄥΟ铏规闂?);
      return;
    }

    try {
      setLoading(true);

      
      const response = await userBanAPI.unban(username, superKey.trim());

      

      const responseData = response.data || response;
      const success = responseData.success || response.status === 'success';
      
      if (success) {
        message.success('閻熸瑱绲介惃婵嬪箣閹邦剙顫?');
        

        await Promise.all([
          loadBannedUsers(),
          loadBanLogs()
        ]);
      } else {
        message.error('閻熸瑱绲介惃婵囧緞鏉堫偉袝: ' + (responseData.message || '闁哄牜浜為悡锟犳煥濞嗘帩鍤?));
      }
    } catch (error) {

      message.error('閻熸瑱绲介惃婵囧緞鏉堫偉袝: ' + error.message);
    } finally {
      setLoading(false);
    }
  };


  const handleCheckBanStatus = async (values) => {
    if (!superKey.trim()) {
      message.warning('閻犲洤鍢查崢娑欐綇閹惧啿寮崇紒鐙呯磿閹﹪宕ㄥΟ铏规闂?);
      return;
    }

    try {
      setLoading(true);
      const { username } = values;
      

      
      const response = await userBanAPI.checkStatus(username.trim(), superKey.trim());

      

      const responseData = response.data || response;
      const success = responseData.success || response.status === 'success';
      
      if (success) {
        setCheckResult(responseData);
        message.success('闁哄被鍎撮妤呭箣閹邦剙顫?);
      } else {
        message.error('闁哄被鍎撮妤佸緞鏉堫偉袝: ' + (responseData.message || '闁哄牜浜為悡锟犳煥濞嗘帩鍤?));
      }
    } catch (error) {

      message.error('闁哄被鍎撮妤佸緞鏉堫偉袝: ' + error.message);
    } finally {
      setLoading(false);
    }
  };


  const handleCleanExpired = async () => {
    if (!superKey.trim()) {
      message.warning('閻犲洤鍢查崢娑欐綇閹惧啿寮崇紒鐙呯磿閹﹪宕ㄥΟ铏规闂?);
      return;
    }

    try {
      setLoading(true);

      
      const response = await userBanAPI.cleanExpired(superKey.trim());

      

      const responseData = response.data || response;
      const success = responseData.success || response.status === 'success';
      
      if (success) {
        const cleanedCount = responseData.cleaned_count || 0;
        message.success(`婵炴挸鎳愰幃濠勨偓鐟版湰閸ㄦ岸鏁嶇仦钘夊綑婵炴挸鎳愰幃?${cleanedCount} 濞戞搩浜ｇ换鍐嫉閻旈娈辩紒鍌欐);
        

        await loadBannedUsers();
      } else {
        message.error('婵炴挸鎳愰幃濠冨緞鏉堫偉袝: ' + (responseData.message || '闁哄牜浜為悡锟犳煥濞嗘帩鍤?));
      }
    } catch (error) {

      message.error('婵炴挸鎳愰幃濠冨緞鏉堫偉袝: ' + error.message);
    } finally {
      setLoading(false);
    }
  };


  const bannedUsersColumns = [
    {
      title: '闁活潿鍔嶉崺娑㈠触?,
      dataIndex: 'username',
      key: 'username',
      render: (text) => <Text strong>{text}</Text>
    },
    {
      title: '閻忓繋鑳堕々锕傚储閻旈攱绀?,
      dataIndex: 'ban_reason',
      key: 'ban_reason',
      ellipsis: true
    },
    {
      title: '閻忓繋鑳堕々锔剧尵鐠囪尙鈧?,
      dataIndex: 'ban_type',
      key: 'ban_type',
      render: (text) => (
        <Tag color={text === '婵﹢鏅茬粻娆戜焊娴ｄ警娲? ? 'red' : 'orange'}>
          {text}
        </Tag>
      )
    },
    {
      title: '閻忓繋鑳堕々锕傚籍閸洘锛?,
      dataIndex: 'banned_at',
      key: 'banned_at'
    },
    {
      title: '闁告帞澧楀﹢锟犲籍閸洘锛?,
      dataIndex: 'banned_until',
      key: 'banned_until',
      render: (text) => text || <Text type="danger">婵﹢鏅茬粻?/Text>
    },
    {
      title: '闁圭瑳鍡╂斀闁?,
      dataIndex: 'banned_by',
      key: 'banned_by'
    },
    {
      title: '闁瑰灝绉崇紞?,
      key: 'action',
      render: (_, record) => (
        <Popconfirm
          title={`缁绢収鍠栭悾鍓ф啺娴ｅ彨鎺斾焊娴ｇ儤鏆忛柟?${record.username} 闁告碍顨愮槐绀皚
          onConfirm={() => handleUnbanUser(record.username)}
          okText="缁绢収鍠栭悾?
          cancelText="闁告瑦鐗楃粔?
        >
          <Button 
            type="primary" 
            size="small" 
            icon={<CheckCircleOutlined />}
            loading={loading}
          >
            閻熸瑱绲介惃?
          </Button>
        </Popconfirm>
      )
    }
  ];


  const banLogsColumns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80
    },
    {
      title: '闁活潿鍔嶉崺娑㈠触?,
      dataIndex: 'username',
      key: 'username',
      render: (text) => <Text strong>{text}</Text>
    },
    {
      title: '闁瑰灝绉崇紞鏃傜尵鐠囪尙鈧?,
      dataIndex: 'action_type',
      key: 'action_type',
      render: (text) => (
        <Tag color={text === 'BAN' ? 'red' : 'green'}>
          {text === 'BAN' ? '閻忓繋鑳堕々? : '閻熸瑱绲介惃?}
        </Tag>
      )
    },
    {
      title: '闁告鍠庡ú?,
      dataIndex: 'reason',
      key: 'reason',
      ellipsis: true
    },
    {
      title: '闁圭瑳鍡╂斀闁?,
      dataIndex: 'banned_by',
      key: 'banned_by'
    },
    {
      title: '闁告帞澧楀﹢锟犲籍閸洘锛?,
      dataIndex: 'banned_until',
      key: 'banned_until',
      render: (text) => text || <Text type="secondary">闁?/Text>
    },
    {
      title: '闁瑰灝绉崇紞鏃堝籍閸洘锛?,
      dataIndex: 'created_at',
      key: 'created_at'
    }
  ];

  return (
    <div style={{ maxWidth: 1200, margin: '20px auto', padding: '0 16px' }}>
      <Card title="闁活潿鍔嶉崺娑氫焊娴ｄ警娲ｇ紒鐙呯磿閹? style={{ marginBottom: 16 }}>
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          {}
          <Card size="small" title="缂佺媴绱曢幃濠囧川濡鍚囬悹?>
            <Space>
              <Input.Password
                placeholder="閻犲洨鏌夌欢顓㈠礂閵壯屽悁闁荤偛妫楅幉宕団偓闈涙閹?
                value={superKey}
                onChange={(e) => setSuperKey(e.target.value)}
                style={{ width: 300 }}
              />
              <Button 
                type="primary" 
                onClick={loadBannedUsers}
                loading={loading}
                icon={<ReloadOutlined />}
              >
                闁告梻濮惧ù鍥极閻楀牆绁?
              </Button>
              <Button 
                onClick={handleCleanExpired}
                loading={loading}
                icon={<DeleteOutlined />}
              >
                婵炴挸鎳愰幃濠冩交閸ャ劍鍩?
              </Button>
            </Space>
          </Card>

          {}
          <Space>
            <Button 
              type="primary" 
              icon={<BanOutlined />}
              onClick={() => setBanModalVisible(true)}
              disabled={!superKey.trim()}
            >
              閻忓繋鑳堕々锕傛偨閵婏箑鐓?
            </Button>
            <Button 
              icon={<SearchOutlined />}
              onClick={() => setCheckModalVisible(true)}
              disabled={!superKey.trim()}
            >
              婵☆偀鍋撻柡灞诲劤婵悂骞€?
            </Button>
          </Space>
        </Space>
      </Card>

      <Tabs defaultActiveKey="1">
        <TabPane tab="鐟滅増鎸告晶鐘典焊娴ｄ警娲ｉ柣顫妽閸? key="1">
          <Card>
            <Table
              columns={bannedUsersColumns}
              dataSource={bannedUsers}
              rowKey="username"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `闁?${total} 濞戞搩浜滈惃婵堢矉娴ｇ儤鏆忛柟瀵革公
              }}
            />
          </Card>
        </TabPane>

        <TabPane tab="閻忓繋鑳堕々锕傚籍閵夈儳绠? key="2">
          <Card 
            extra={
              <Button 
                onClick={() => loadBanLogs()}
                loading={loading}
                icon={<ReloadOutlined />}
                disabled={!superKey.trim()}
              >
                闁告帡鏀遍弻濠囧籍閵夈儳绠?
              </Button>
            }
          >
            <Table
              columns={banLogsColumns}
              dataSource={banLogs}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 20,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `闁?${total} 闁哄鍓濆Λ鈺勭疀濮?
              }}
            />
          </Card>
        </TabPane>
      </Tabs>

      {}
      <Modal
        title="閻忓繋鑳堕々锕傛偨閵婏箑鐓?
        open={banModalVisible}
        onCancel={() => setBanModalVisible(false)}
        footer={null}
        width={500}
      >
        <Form
          form={banForm}
          layout="vertical"
          onFinish={handleBanUser}
        >
          <Form.Item
            name="username"
            label="闁活潿鍔嶉崺娑㈠触?
            rules={[{ required: true, message: '閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃? }]}
          >
            <Input placeholder="閻犲洨鏌夌欢顓㈠礂閵夘煈娲ｉ悘蹇庤兌椤╋箓鎯冮崟顓熸殢闁规潙鍢查幃? />
          </Form.Item>

          <Form.Item
            name="reason"
            label="閻忓繋鑳堕々锕傚储閻旈攱绀?
          >
            <TextArea 
              rows={3} 
              placeholder="閻犲洨鏌夌欢顓㈠礂閵夈儳娈辩紒鍌欑鐢偊宕堕悪鍛闁告瑯鍨堕埀顒€顧€缁辨繃顪€濡鍚囬柨娑欐皑椤撴悂鎮堕崱妤佸枀閻忓繋鑳堕々锕傛晬?
            />
          </Form.Item>

          <Form.Item
            name="isPermanent"
            valuePropName="checked"
          >
            <Switch 
              checkedChildren="婵﹢鏅茬粻娆戜焊娴ｄ警娲? 
              unCheckedChildren="濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲? 
            />
          </Form.Item>

          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) => 
              prevValues.isPermanent !== currentValues.isPermanent
            }
          >
            {({ getFieldValue }) =>
              !getFieldValue('isPermanent') && (
                <Form.Item
                  name="duration"
                  label="閻忓繋鑳堕々锕傚籍閸洘姣愰柨娑樼墕閸ㄥ酣鏌﹂悤鍌滅"
                  rules={[{ required: true, message: '閻犲洨鏌夌欢顓㈠礂閵夈儳娈辩紒鍌欑劍濡炲倿姊? }]}
                >
                  <InputNumber 
                    min={1} 
                    placeholder="閻犲洨鏌夌欢顓㈠礂閵夈儳娈辩紒鍌欑劍濡炲倿姊归崠锛勭闁告帒妫濋幐鎾绘晬?
                    style={{ width: '100%' }}
                    addonAfter="闁告帒妫濋幐?
                  />
                </Form.Item>
              )
            }
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                缁绢収鍠涢鑽や焊娴ｄ警娲?
              </Button>
              <Button onClick={() => setBanModalVisible(false)}>
                闁告瑦鐗楃粔?
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {}
      <Modal
        title="婵☆偀鍋撻柡灞诲劚閻ㄦ繄绮嬫担鍝勑﹂柟?
        open={checkModalVisible}
        onCancel={() => {
          setCheckModalVisible(false);
          setCheckResult(null);
          checkForm.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form
          form={checkForm}
          layout="vertical"
          onFinish={handleCheckBanStatus}
        >
          <Form.Item
            name="username"
            label="闁活潿鍔嶉崺娑㈠触?
            rules={[{ required: true, message: '閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃? }]}
          >
            <Input placeholder="閻犲洨鏌夌欢顓㈠礂閵夘煈娲ｉ柡灞诲劥椤曟鎯冮崟顓熸殢闁规潙鍢查幃? />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                闁哄被鍎撮妤呮偐閼哥鍋?
              </Button>
              <Button onClick={() => {
                setCheckResult(null);
                checkForm.resetFields();
              }}>
                闂佹彃绉堕悿?
              </Button>
            </Space>
          </Form.Item>
        </Form>

        {checkResult && (
          <Card 
            title="闁哄被鍎撮妤冪磼閹惧浜? 
            size="small"
            style={{ 
              marginTop: 16,
              backgroundColor: checkResult.is_banned ? '#fff2f0' : '#f6ffed',
              borderColor: checkResult.is_banned ? '#ffb3b3' : '#b7eb8f'
            }}
          >
            <Space direction="vertical" style={{ width: '100%' }}>
              <div>
                <Text strong>闁活潿鍔嶉崺娑㈠触? </Text>
                <Text>{checkResult.target_username}</Text>
              </div>
              
              <div>
                <Text strong>閻忓繋鑳堕々锕傛偐閼哥鍋? </Text>
                <Tag color={checkResult.is_banned ? 'red' : 'green'}>
                  {checkResult.is_banned ? '鐎瑰憡褰冮惃婵堢矉? : '闁哄牜浜滈惃婵堢矉?}
                </Tag>
              </div>

              <div>
                <Text strong>闁绘鍩栭埀顑挎祰椤曗晠寮? </Text>
                <Text>{checkResult.status_message}</Text>
              </div>

              {checkResult.is_banned && (
                <>
                  <div>
                    <Text strong>閻忓繋鑳堕々锕傚储閻旈攱绀? </Text>
                    <Text>{checkResult.ban_reason}</Text>
                  </div>

                  <div>
                    <Text strong>閻忓繋鑳堕々锔剧尵鐠囪尙鈧? </Text>
                    <Text>{checkResult.ban_type}</Text>
                  </div>

                  <div>
                    <Text strong>閻忓繋鑳堕々锕傚籍閸洘锛? </Text>
                    <Text>{checkResult.banned_at}</Text>
                  </div>

                  <div>
                    <Text strong>闁告帞澧楀﹢锟犲籍閸洘锛? </Text>
                    <Text>{checkResult.banned_until || '婵﹢鏅茬粻?}</Text>
                  </div>

                  <div>
                    <Text strong>闁圭瑳鍡╂斀闁? </Text>
                    <Text>{checkResult.banned_by}</Text>
                  </div>
                </>
              )}
            </Space>
          </Card>
        )}
      </Modal>
    </div>
  );
};

export default UserBanManager; 
