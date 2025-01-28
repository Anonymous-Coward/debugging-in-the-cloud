# Debugging in the cloud

## What it's all about

"Works on my machine" - right?

But that's not going to help your users. So what do you do when something does work on your machine, but consistently (or inconsistently) fails when deployed in a cloud environment like the one where it is supposed to run?

IME, typical problems in understanding how things fail are TLS-encrypted traffic, excessively long logs, long cycle time between code changes and redeployment and not actually being able to run a debugger against the application that fails. Quite often, the error is a highly collaborative one - tiny, subtle inconsistencies between distinct applications contribute to causing the error.

So what do you do? Quite often, you keep adding logging statements and redeploy, in the hopes that eventually extensive logging will reveal the relevant inconsistency or race condition that causes the error - and it usually does. But it's an extremely tedious and long lasting process.

So, is there a better way?

I believe there is. In this presentation, a few helpful tricks in fighting precisely the problems I described above are illustrated.
