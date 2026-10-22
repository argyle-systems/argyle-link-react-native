require "json"
require "digest"
require "fileutils"
require "tmpdir"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

# Argyle iOS SDK release to vendor. The checksum is the `checksum` from Package.swift at the
# matching tag in argyle-systems/argyle-link-ios (SHA-256 of Argyle.xcframework.zip).
argyle_sdk_version = "5.16.0"
argyle_sdk_sha256 = "1f2a25df49ae822ef44073e2c119659b6580377d30abaa6286767208874b0b32"

frameworks_dir = File.join(__dir__, "ios", "Frameworks")
xcframework_path = File.join(frameworks_dir, "Argyle.xcframework")
stamp_path = File.join(frameworks_dir, ".argyle-sdk")

unless File.directory?(xcframework_path) && File.exist?(stamp_path) && File.read(stamp_path) == argyle_sdk_sha256
  url = "https://github.com/argyle-systems/argyle-link-ios/releases/download/#{argyle_sdk_version}/Argyle.xcframework.zip"
  Pod::UI.puts "[argyle-plugin-react-native] Downloading Argyle iOS SDK #{argyle_sdk_version}"

  Dir.mktmpdir do |tmp|
    zip_path = File.join(tmp, "Argyle.xcframework.zip")
    system("curl", "--fail", "--silent", "--show-error", "--location", "--retry", "3", "--output", zip_path, url) ||
      raise("[argyle-plugin-react-native] Failed to download #{url}")

    actual_sha256 = Digest::SHA256.file(zip_path).hexdigest
    unless actual_sha256 == argyle_sdk_sha256
      raise "[argyle-plugin-react-native] Checksum mismatch for #{url}: expected #{argyle_sdk_sha256}, got #{actual_sha256}"
    end

    system("unzip", "-q", zip_path, "-d", tmp) ||
      raise("[argyle-plugin-react-native] Failed to unzip #{zip_path}")

    FileUtils.rm_rf(frameworks_dir)
    FileUtils.mkdir_p(frameworks_dir)
    FileUtils.mv(File.join(tmp, "Argyle.xcframework"), xcframework_path)
    File.write(stamp_path, argyle_sdk_sha256)
  end
end

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
  s.platforms    = { :ios => "15.0" }
  s.source       = { :git => "https://github.com/argyle-systems/argyle-plugin-react-native.git", :tag => "#{s.version}" }

  s.source_files = "ios/*.{h,m,swift}"
  s.requires_arc = true
  s.vendored_frameworks = "ios/Frameworks/Argyle.xcframework"

  s.dependency "React"
end
