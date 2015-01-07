var gulp = require('gulp');
//var del = require('del');


gulp.task('scripts', function() {

    gulp.src('./resources.resources.bower_components/**/*.js').pipe(gulp.dest('./resources/scripts/lib'));
});


gulp.task('default', ['scripts']);