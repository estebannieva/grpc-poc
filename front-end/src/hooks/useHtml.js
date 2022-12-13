import { useEffect } from 'react';

export const useHtml = (handleClick, ...classes) => {
  useEffect(() => {
    window.addEventListener('click', (ev) => handleClick(ev))
    classes.forEach((className) => {
      document.documentElement.classList.add(className);
    })
  }, []);
}
