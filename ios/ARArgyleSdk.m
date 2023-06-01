#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(ARArgyleSdk, RCTEventEmitter)

RCT_EXTERN_METHOD(updateToken:(NSString *)newToken)
RCT_EXTERN_METHOD(start: (NSDictionary *)config)
RCT_EXTERN_METHOD(close)

@end
