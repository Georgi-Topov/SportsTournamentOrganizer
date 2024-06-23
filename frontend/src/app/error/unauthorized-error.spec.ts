import { UnauthorizedError } from './unauthorized-error';

describe('UnauthorizedError', () => {
  it('should create an instance', () => {
    expect(new UnauthorizedError()).toBeTruthy();
  });
});
