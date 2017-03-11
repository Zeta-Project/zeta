var gulp        = require('gulp');
var browserSync = require('browser-sync').create();

// Static Server + watching files
gulp.task('watch', function() {
  browserSync.init(null, {
    proxy: "http://localhost:8080",
    port: 3000,
    files: ["src/**/*.*"]
  });
  gulp.watch("src/*.html").on('change', browserSync.reload);
});
