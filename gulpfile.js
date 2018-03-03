/*
 *  目录说明 
 *  / --
 *    -- scss
 *    -- css
 *    -- js
 *    -- img
 *    -- fonts
 *    -- dist     connect构建测试服务器目录。
 *    -- WEB-INF
 *       -- page  html文件存放位置,使用gulp构建服务器，复制html到dist目录下。
 */


// 载入外挂
var gulp    = require('gulp'), 
    plugins = require('gulp-load-plugins')();

var map = {
  port    : 8000,
  root    : 'static',
  scss    : 'static/scss/',
  styles  : 'static/css/',
  images  : 'static/img/',
  scripts : 'static/js/',
  font    : 'static/font/',
  dist    : 'src/main/webapp',
  page    : 'src/main/webapp/WEB-INF/page/'
}
// 样式
gulp.task('sass', function() { 
  return gulp.src(map.scss + '**/*.scss')
    .pipe(plugins.sass({
      outputStyle: 'nested'
    }).on('error', plugins.sass.logError))
    // .pipe(plugins.sourcemaps.write())
    .pipe(plugins.autoprefixer({
      browsers: [
        'last 3 versions',
        'ios 6',
        'Android >= 2.3'
      ]
    }))
    .pipe(gulp.dest(map.styles))
    .pipe(gulp.dest(map.dist + '/css'));
});

// build 任务
/* ----------------------------------------------------------------*/
gulp.task('css', function(){
  return gulp.src(map.styles + '**/*.css')
    .pipe(gulp.dest(map.dist + '/css'));
});

// 脚本  暂时不做压缩合并的操作，
gulp.task('scripts', function() { 
 return gulp.src(map.scripts + '**/*')
    .pipe(gulp.dest(map.dist + '/js'));
});
 
gulp.task('imgs', function() { 
  return gulp.src(map.images + '**/*')
    .pipe(gulp.dest(map.dist + '/img'));
});

// font
gulp.task('fonts', function() { 
  return gulp.src(map.font + '**/*') 
    .pipe(gulp.dest(map.dist + '/font'));
});

gulp.task('htmls', function() { 
  return gulp.src(map.root + '/**/*.html') 
    .pipe(gulp.dest(map.page));
});

/* ----------------------------------------------------------------*/
gulp.task("revFont", function(){
  return gulp.src(map.font + "*")
    .pipe(plugins.rev())
    .pipe(plugins.rev.manifest())
    .pipe(gulp.dest(map.root + '/rev/font'));
})

gulp.task("revImg", function(){
  return gulp.src(map.images + "*")
    .pipe(plugins.rev())
    .pipe(plugins.rev.manifest())
    .pipe(gulp.dest(map.root + '/rev/images'));
})
/* ----------------------------------------------------------------*/
// 清理
/*gulp.task('clean', function() { 
  return gulp.src([map.dist], {read: true})
    .pipe(plugins.rimraf());
});*/
 
//使用connect启动一个Web服务器
gulp.task('connect', function() {
  var open = require('open');
  plugins.connect.server({
    root : map.root,
    port : map.port,
    livereload: true
  });
  open('http://localhost:' + map.port+'/console/index.html');
});  

//重啓服務
gulp.task('reload', function() {
  return gulp.src(map.root + "/**/*.*")
    .pipe(plugins.connect.reload());
});

// 预设任务
gulp.task('default',['sass'],function(){
  gulp.start('watch','connect');
});

//构建 
gulp.task('build', function(){
  gulp.start('sass', 'scripts', 'fonts', 'htmls', 'imgs','css')
}) 
// 看守
gulp.task('watch', function() {
  gulp.watch(map.scss   + '**/*.scss',  ['sass']);
  gulp.watch(map.scripts+ '**/*.js',    ['reload','build']);
  gulp.watch(map.root   + '/**/*.htm*', ['reload','build']);
  gulp.watch(map.font   + '**/*',       ['reload','build']);
  gulp.watch(map.styles + '**/*.css',   ['reload','build']);
});
