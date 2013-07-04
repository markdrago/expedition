# Expedition

Expedition is a web app which connects to crucible via its REST API and produces a chart which shows the cost of code
reviews over time based on the size of the review and how long they have been open.

## Goal

My goal with writing expedition is to give my team the insight they need to understand how crucible and code reviews
affect their ability to work in small batches.  The name "expedition" is in reference to the less commonly used
definition "efficient promptness", as in "expedite".  For more information about small batches and why they're a good
idea I recommend the book "The Principles of Product Development Flow: Second Generation Lean Product Development" by
Donald G. Reinertsen.

## Math

Expedition relies on a single calculation which produces a number based on the size and age of a code review
throughout the time that it is open.  The goal of this calculation is to represent the instantaneous cost of that code
review existing in its open state.  That instantaneous cost, in my opinion, goes up as the size of the code review
increases and as it ages.  The formula I came up with to model this is:

    s = number of files in review (proxy for code review size)
    t = number of work hours review has been open
    size_component = s * ln(s) + 1
    time_component = t * ln(t) + 1
    total_cost = size_component * (time_component * .5)

This formula is just something I came up with by playing with it for a bit.  Using nlog(n) felt right to me to try and
model the cost of a code review as its size increases and as it ages.  I used ln instead of log just because when I
looked at the numbers produced by each, log() looked like it was increasing faster than I thought it should - ln just
seemed to work out better.  The +1s are there to avoid multiplying a 0 in to the calculation.  The .5 on the time
component is there, again, just because without it the costs seemed to balloon faster than I really think they should.

## Tech
Expedition uses the Play! framework, Scala, and D3.js.  The data sent from the server to the client is a server-sent
event which is sent over a websocket.  In this way the server only hits the crucible API on its own schedule and sends
updated information out to any connected clients when there is new data to send.
