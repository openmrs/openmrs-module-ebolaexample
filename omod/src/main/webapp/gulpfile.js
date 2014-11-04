var gulp = require('gulp');
//var del = require('del');


gulp.task('scripts', function() {

    gulp.src('./bower_components/**/*.min.js').pipe(gulp.dest('./pages/js'));
});


gulp.task('default', ['scripts']);