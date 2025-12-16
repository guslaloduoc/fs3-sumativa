import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      include: ['src/**/*.ts'],
      exclude: [
        'src/**/*.spec.ts',
        'src/**/main.ts',
        'src/**/test-setup.ts',
        'src/**/*.d.ts',
        'src/**/*.config.ts'
      ],
      thresholds: {
        lines: 93,
        functions: 93,
        branches: 93,
        statements: 93
      }
    },
    globals: true,
    environment: 'jsdom',
    setupFiles: ['src/test-setup.ts']
  }
});
