import { HttpInterceptorFn } from '@angular/common/http';
import { tap, finalize } from 'rxjs/operators';

export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  const startTime = Date.now();
  const requestId = Math.random().toString(36).substring(7);

  console.log(`[HTTP ${requestId}] ➡️ ${req.method} ${req.url}`);

  return next(req).pipe(
    tap({
      next: (event: any) => {
        if (event.type === 4) { // HttpEventType.Response
          const elapsed = Date.now() - startTime;
          console.log(`[HTTP ${requestId}] ✅ ${req.url} - ${event.status} (${elapsed}ms)`);
          console.log(`[HTTP ${requestId}] Response:`, event.body);
        }
      },
      error: (error: any) => {
        const elapsed = Date.now() - startTime;
        console.error(`[HTTP ${requestId}] ❌ ${req.url} - Error (${elapsed}ms)`, error);
      }
    }),
    finalize(() => {
      const elapsed = Date.now() - startTime;
      console.log(`[HTTP ${requestId}] 🏁 Finalized (${elapsed}ms)`);
    })
  );
};
