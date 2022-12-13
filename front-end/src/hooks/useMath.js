export const useMath = () => {

  const pct = (value, total) => {
    let str = `${(value / total) * 100}`
    return parseInt(str);
  }

  return [pct];
}
