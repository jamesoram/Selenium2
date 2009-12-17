# Platform checks

def windows?
  RUBY_PLATFORM.downcase.include?("win32")
end

def mac?
  RUBY_PLATFORM.downcase.include?("darwin")
end

def linux?
  RUBY_PLATFORM.downcase.include?("linux")
end

def cygwin?
  RUBY_PLATFORM.downcase.include?("cygwin")
end

def classpath_separator?
  if cygwin? then
    ";"
  else
    File::PATH_SEPARATOR
  end
end

def all?
  true
end

# Checking for particular applications 

def present?(arg)
  prefixes = ENV['PATH'].split(File::PATH_SEPARATOR)

  matches = prefixes.select do |prefix|
    File.exists?(prefix + File::SEPARATOR + arg)
  end

  matches.length > 0
end

def java?
  present?("java") || present?("java.exe")
end

def javac?
  present?("javac") || present?("javac.exe")
end

def jar?
  present?("jar") || present?("jar.exe")
end

# Think of the confusion if we called this "g++"
def gcc?
  linux? && present?("g++") 
end

def python?
  present?("python") || present?("python.exe")
end

def msbuild?
  windows? && present?("msbuild.exe")
end

def xcode?
  return mac? && present?('xcodebuild')
end

def iPhoneSDKPresent?
  return false unless xcode?
  sdks = sh "xcodebuild -showsdks 1>/dev/null 2>/dev/null", :verbose => false
  !!(sdks =~ /iphonesimulator/)
  return true
end

def iPhoneSDK?
  return nil unless iPhoneSDKPresent?
  if $iPhoneSDK == nil then
    cmd = open("|xcodebuild -showsdks | grep iphonesimulator | awk '{print $7}'")
    sdks = cmd.readlines.map {|x| x.gsub(/\b(.*)\b.*/m, '\1')}
    cmd.close
    if ENV['IPHONE_SDK_VERSION'] == nil then
      puts "No SDK specified, using #{sdks[0]}"
      $iPhoneSDK = sdks[0]
    else
      $iPhoneSDK = "iphonesimulator#{ENV['IPHONE_SDK_VERSION']}"
      puts "Testing for SDK #{$iPhoneSDK}"
      if sdks.index($iPhoneSDK) == nil then
        puts "...#{$iPhoneSDK} not found; Defaulting to #{sdks[0]}"
        $iPhoneSDK = sdks[0]
      end
    end
    puts "Using iPhoneSDK: #{$iPhoneSDK}"
  end
  $iPhoneSDK
end

def iPhoneSDKVersion?
  sdk = iPhoneSDK?
  if sdk != nil then
    sdk.gsub(/iphonesimulator(.*)/, '\1')
  end
end
