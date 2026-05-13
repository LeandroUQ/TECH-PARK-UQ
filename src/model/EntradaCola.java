package model;

public class EntradaCola implements Comparable<EntradaCola> {

     private final Visitante visitante;
     private final int prioridad;  // 1=Fast-Pass, 2=General
     private final long timestamp; // desempate FIFO

     public EntradaCola(Visitante visitante, int prioridad) {
         this.visitante = visitante;
         this.prioridad = prioridad;
         this.timestamp = System.nanoTime();
     }

     @Override
     public int compareTo(EntradaCola otro) {
         int cmp = Integer.compare(this.prioridad, otro.prioridad);
         return cmp != 0 ? cmp : Long.compare(this.timestamp, otro.timestamp);
     }

     public Visitante getVisitante() { return visitante; }
     public int getPrioridad() { return prioridad; }
 }


