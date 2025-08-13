import React, { useState, useEffect } from 'react';
import { Alert } from 'antd';
import { CloseOutlined } from '@ant-design/icons';

const ErrorNotification = () => {
  const [errors, setErrors] = useState([]);


  const addError = (message) => {

    const id = Date.now() + Math.random();
    const newError = {
      id,
      message,
      timestamp: Date.now()
    };
    
    setErrors(prev => [...prev, newError]);
    

    setTimeout(() => {
      removeError(id);
    }, 3000);
  };


  const removeError = (id) => {
    setErrors(prev => prev.filter(error => error.id !== id));
  };


  useEffect(() => {
    window.showError = addError;
    

    return () => {
      delete window.showError;
    };
  }, []);

  if (errors.length === 0) return null;

  return (
    <div style={{
      position: 'fixed',
      top: '20px',
      right: '20px',
      zIndex: 9999,
      maxWidth: '400px',
      width: '100%'
    }}>
      {errors.map((error, index) => (
        <div
          key={error.id}
          style={{
            marginBottom: '8px',
            animation: 'slideInRight 0.3s ease-out'
          }}
        >
          <Alert
            message={error.message}
            type="error"
            showIcon
            closable
            onClose={() => removeError(error.id)}
            style={{
              backgroundColor: 'rgba(255, 77, 79, 0.9)',
              border: '1px solid #ff4d4f',
              borderRadius: '8px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
              backdropFilter: 'blur(10px)',
              WebkitBackdropFilter: 'blur(10px)'
            }}
            action={
              <CloseOutlined
                onClick={() => removeError(error.id)}
                style={{ color: '#fff', cursor: 'pointer' }}
              />
            }
          />
        </div>
      ))}
      <style>{`
        @keyframes slideInRight {
          from {
            opacity: 0;
            transform: translateX(100%);
          }
          to {
            opacity: 1;
            transform: translateX(0);
          }
        }
      `}</style>
    </div>
  );
};

export default ErrorNotification; 
