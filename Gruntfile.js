module.exports = function(grunt) {
    grunt.initConfig({
        openui5_preload: {
            component: {
                options: {
                    resources: {
                        cwd: 'src/main/webapp/frontend',
                        prefix: 'frontend'
                    },
                    dest: 'src/main/webapp/frontend'
                },
            components: 'frontend'
            }
        }
    });
    grunt.loadNpmTasks('grunt-openui5');

    grunt.registerTask('default', ['openui5_preload']);
}
