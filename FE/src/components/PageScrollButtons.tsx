import React, { useEffect, useState } from 'react';

export const PageScrollButtons: React.FC = () => {
  const [scrollState, setScrollState] = useState({ canScrollUp: false, canScrollDown: false });

  useEffect(() => {
    const updateScrollState = () => {
      const documentHeight = document.documentElement.scrollHeight;
      const scrollTop = window.scrollY;
      setScrollState({
        canScrollUp: scrollTop > 120,
        canScrollDown: scrollTop + window.innerHeight < documentHeight - 120,
      });
    };

    updateScrollState();
    window.addEventListener('scroll', updateScrollState, { passive: true });
    window.addEventListener('resize', updateScrollState);
    const resizeObserver = new ResizeObserver(updateScrollState);
    resizeObserver.observe(document.documentElement);

    return () => {
      window.removeEventListener('scroll', updateScrollState);
      window.removeEventListener('resize', updateScrollState);
      resizeObserver.disconnect();
    };
  }, []);

  if (!scrollState.canScrollUp && !scrollState.canScrollDown) return null;

  return <div className="page-scroll-buttons">
    {scrollState.canScrollUp && <button type="button" aria-label="Cuộn lên đầu trang" title="Lên đầu trang" onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}>↑</button>}
    {scrollState.canScrollDown && <button type="button" aria-label="Cuộn xuống cuối trang" title="Xuống cuối trang" onClick={() => window.scrollTo({ top: document.documentElement.scrollHeight, behavior: 'smooth' })}>↓</button>}
  </div>;
};
