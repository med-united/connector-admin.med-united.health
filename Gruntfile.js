module.exports = function(grunt) {
    grunt.initConfig({
        openui5_preload: {
            component: {
                options: {
                    resources: {
                        cwd: 'src/main/webapp/blueprint',
                        prefix: 'blueprint'
                    },
                    dest: 'src/main/webapp/blueprint'
                },
            components: 'blueprint'
            }
        }
    });
    grunt.loadNpmTasks('grunt-openui5');

    grunt.registerTask('default', ['openui5_preload']);
}
