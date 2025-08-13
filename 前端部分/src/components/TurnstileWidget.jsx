import React, { useEffect, useRef, useState } from 'react';

const TurnstileWidget = ({ 
    siteKey = '0x4AAAAAABo9nulg02Izg0O_',
    theme = 'auto',
    language = 'zh-CN',
    onVerify,
    onError,
    onExpire,
    onLoad,
    resetTrigger = 0
}) => {
    const containerRef = useRef(null);
    const widgetIdRef = useRef(null);
    const [isLoaded, setIsLoaded] = useState(false);
    const [isReady, setIsReady] = useState(false);
    const mountedRef = useRef(true);


    const checkTurnstileReady = () => {
        return window.turnstile && typeof window.turnstile.render === 'function';
    };


    const safeSetState = (setter, value) => {
        if (mountedRef.current) {
            setter(value);
        }
    };


    const renderWidget = () => {
        if (!checkTurnstileReady() || !containerRef.current || !mountedRef.current) {

            return;
        }

        try {

            if (widgetIdRef.current !== null) {
                try {
                    window.turnstile.reset(widgetIdRef.current);
                } catch (e) {

                }
                widgetIdRef.current = null;
            }

            const id = window.turnstile.render(containerRef.current, {
                sitekey: siteKey,
                theme: theme,
                language: language,
                callback: (token) => {
                    if (mountedRef.current) {

                        onVerify && onVerify(token);
                    }
                },
                'error-callback': (error) => {
                    if (mountedRef.current) {

                        onError && onError(error);
                    }
                },
                'expired-callback': () => {
                    if (mountedRef.current) {

                        onExpire && onExpire();
                    }
                },
                'timeout-callback': () => {
                    if (mountedRef.current) {

                        onError && onError('timeout');
                    }
                }
            });

            widgetIdRef.current = id;

            onLoad && onLoad();

        } catch (error) {

            onError && onError('render_failed');
        }
    };


    const resetWidget = () => {
        if (checkTurnstileReady() && widgetIdRef.current !== null && mountedRef.current) {
            try {
                window.turnstile.reset(widgetIdRef.current);

            } catch (error) {

            }
        }
    };


    useEffect(() => {
        const checkReady = () => {
            if (checkTurnstileReady()) {
                safeSetState(setIsReady, true);
                return true;
            }
            return false;
        };

        if (checkReady()) {
            return;
        }


        const interval = setInterval(() => {
            if (checkReady()) {
                clearInterval(interval);
            }
        }, 100);


        const timeout = setTimeout(() => {
            clearInterval(interval);
            if (mountedRef.current && !checkTurnstileReady()) {

                onError && onError('api_load_timeout');
            }
        }, 10000);

        return () => {
            clearInterval(interval);
            clearTimeout(timeout);
        };
    }, []);


    useEffect(() => {
        if (isReady && !isLoaded) {
            safeSetState(setIsLoaded, true);

            const timeoutId = setTimeout(renderWidget, 0);
            return () => clearTimeout(timeoutId);
        }
    }, [isReady, isLoaded, siteKey]);


    useEffect(() => {
        if (resetTrigger > 0 && isLoaded) {
            resetWidget();
        }
    }, [resetTrigger]);


    useEffect(() => {
        return () => {
            mountedRef.current = false;
            if (widgetIdRef.current !== null) {
                try {


                    widgetIdRef.current = null;
                } catch (error) {

                }
            }
        };
    }, []);

    return (
        <div style={{ textAlign: 'center', margin: '20px 0' }}>
            <div 
                ref={containerRef}
                style={{ 
                    minHeight: '65px',
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center'
                }}
                key={resetTrigger}
            >
                {!isReady && (
                    <div style={{ 
                        color: '#666', 
                        fontSize: '14px',
                        padding: '20px'
                    }}>
                        妫ｅ啯鏁?婵繐绲藉﹢顏堝礉閻樼儤绁板ù婊呭劋濠р偓濡ょ姴鐭侀惁?..
                    </div>
                )}
                {isReady && !isLoaded && (
                    <div style={{ 
                        color: '#666', 
                        fontSize: '14px',
                        padding: '20px'
                    }}>
                        妫ｅ啫绠?婵繐绲藉﹢顏堝礆濠靛棭娼楅柛鏍ㄧ墵閻涙瑧鎷?..
                    </div>
                )}
            </div>
            
            {isLoaded && (
                <div style={{ marginTop: '10px' }}>
                    <button 
                        type="button"
                        onClick={resetWidget}
                        style={{
                            background: '#6c757d',
                            color: 'white',
                            border: 'none',
                            padding: '6px 12px',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            fontSize: '12px'
                        }}
                    >
                        妫ｅ啯鏁?闂佹彃绉堕悿鍡橆殽瀹€鍐
                    </button>
                </div>
            )}
        </div>
    );
};

export default TurnstileWidget; 
