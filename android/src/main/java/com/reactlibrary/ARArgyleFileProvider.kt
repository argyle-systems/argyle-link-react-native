package com.reactlibrary

import androidx.core.content.FileProvider

// Own subclass so the manifest entry doesn't clash with the host app's androidx FileProvider
internal class ARArgyleFileProvider : FileProvider()
