package io.github.plugindustry.wheelcore.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class BipartiteGraph {
    private final int U;
    private final int V;
    private final List<Edge>[] outs;
    private final boolean[] match;
    private final int[] dep;

    @SuppressWarnings("unchecked")
    public BipartiteGraph(int U, int V) {
        this.U = U;
        this.V = V;
        outs = new ArrayList[U + V];
        for (int i = 0; i < U + V; ++i) outs[i] = new ArrayList<>();
        match = new boolean[U + V];
        dep = new int[U + V];
    }

    public void addEdge(int u, int v) {
        Edge e1 = new Edge(v);
        Edge e2 = new Edge(u);
        e1.paired = e2;
        e2.paired = e1;
        outs[u].add(e1);
        outs[v].add(e2);
    }

    private List<Integer> bfs() {
        for (int i = 0; i < U + V; ++i) dep[i] = -1;
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < U; ++i)
            if (!match[i]) {
                dep[i] = 0;
                queue.push(i);
            }

        int lim = Integer.MAX_VALUE;
        List<Integer> ends = new ArrayList<>();
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (dep[cur] > lim) continue;
            if (cur >= U && !match[cur]) {
                lim = dep[cur];
                ends.add(cur);
            }
            if (dep[cur] == lim) continue;
            for (Edge edge : outs[cur]) {
                if ((cur < U && edge.match) || (cur >= U && !edge.match)) continue;
                if (dep[edge.to] != -1) continue;
                dep[edge.to] = dep[cur] + 1;
                queue.push(edge.to);
            }
        }

        return ends;
    }

    private boolean dfs(int cur) {
        if (dep[cur] == 0) {
            if (match[cur]) return false;
            else {
                match[cur] = true;
                return true;
            }
        }

        boolean flag = false;
        for (Edge edge : outs[cur]) {
            if (edge.used) continue;
            if ((cur < U && !edge.match) || (cur >= U && edge.match)) continue;
            if (dep[edge.to] != dep[cur] - 1) continue;
            edge.used = edge.paired.used = true;
            if (dfs(edge.to)) {
                flag = true;
                edge.match = edge.paired.match = !edge.match;
                match[cur] = true;
                break;
            }
        }

        return flag;
    }

    public int maxMatch() {
        for (List<Edge> out : outs) for (Edge edge : out) edge.match = false;
        for (int i = 0; i < U + V; ++i) match[i] = false;

        while (true) {
            List<Integer> ends = bfs();
            if (ends.isEmpty()) break;

            for (List<Edge> out : outs) for (Edge edge : out) edge.used = false;
            for (int end : ends) dfs(end);
        }

        int cnt = 0;
        for (List<Edge> out : outs) for (Edge edge : out) if (edge.match) ++cnt;
        return cnt;
    }

    public static class Edge {
        public final int to;
        public boolean match = false;
        public boolean used = false;
        public Edge paired;

        public Edge(int to) {
            this.to = to;
        }
    }
}