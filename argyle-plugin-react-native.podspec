require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "argyle-plugin-react-native"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                    argyle-plugin-react-native
                   DESC
  s.homepage     = "https://argyle.io"
  s.license      = "Apache 2.0"
  s.authors      = { "Argyle Systems" => "argyle@argyle.io" }
  s.platforms    = { :ios => "12.0", :tvos => "12.0" }
  s.source       = { :git => "https://github.com/argyle-systems/argyle-plugin-react-native.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency 'Argyle', '5.8.1'
end

