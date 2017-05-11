# Android GCD

[ ![Download](https://api.bintray.com/packages/hermanliang/maven/android-gcd/images/download.svg) ](https://bintray.com/hermanliang/maven/android-gcd/_latestVersion)

Using iOS-like Grand Central Dispatch (GCD) in Android. 

## Usage

### Add Async Task to Queue

Add task to UI queue
```java
DispatchQueue.main().async(new Callable<Task<Void>>() {
    @Override
    public Task<Void> call() throws Exception {
        // Add your UI task here
        return null;
    }
});
```

Add task to global background queue
```java
DispatchQueue.global().async(new Callable<Task<Void>>() {
    @Override
    public Task<Void> call() throws Exception {
        // Add your background task here
        return null;
    }
});
```

Add task to a specific backgroud queue
```java
DispatchQueue.global(groupId).async(Callable);
```

### Add Sync Task to Queue
```java
// Run in UI queue
DispatchQueue.main().sync(Callable);

// Run in global background queue
DispatchQueue.global().sync(Callable);

// Run in specific background queue
DispatchQueue.global(groupId).sync(Callable);
```

### Add Task with Delay
```java
// Run in UI queue with delay
DispatchQueue.main().async(Callable, delayInMilliSeconds);

// Run in global background queue with delay
DispatchQueue.global().async(Callable, delayInMilliSeconds);

// Run in specific background queue with delay
DispatchQueue.global(groupId).async(Callable, delayInMilliSeconds);
```

### Cancel Tasks in Queue
```java
// Cancel all tasks in UI queue
DispatchQueue.main().cancel();

// Cancel all tasks in global background queue
DispatchQueue.global().cancel();

// Cancel all tasks in specific background queue
DispatchQueue.global(groupId).cancel();
```

## Download

```groovy
dependencies {
  compile 'com.hl:android-gcd:0.2.0'
}
```

## Lincense
```
MIT License

Copyright (c) 2017 Herman Liang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```