-module(jcomplex).
-export([foo/1, bar/1]).

foo(X) ->
  call_jnode({foo, X}).
bar(Y) ->
  call_jnode({bar, Y}).

call_jnode(Msg) ->
  {java, j1@alpha} ! {call, self(), Msg},
  receive
    Any ->
      io:format("~p~n", [Any])
  end.
