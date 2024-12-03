import { setTimeout } from 'timers';

export async function sleep(milliseconds) {
  return new Promise<void>((resolve) => {
    setTimeout(() => resolve(), milliseconds);
  });
}
