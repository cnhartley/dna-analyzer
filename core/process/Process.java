/**
 * 
 */
package core.process;

import java.util.EventListener;
import java.util.EventObject;
import java.util.LinkedList;

/**
 * @author MRR54
 *
 */
public abstract class Process<RESULT> implements Runnable {

    private LinkedList<ProcessListener> listeners =
            new LinkedList<ProcessListener>();
    
    private boolean processing = false;
    private boolean needToCancel = false;
    private RESULT result = null;
    
    
    /**
     * For this implementation, the <I>run()</I> method cannot be overwritten.
     * Instead, override the <I>process()</I> method to execute the desired
     * processes.
     * 
     * @see #process()
     * @see java.lang.Runnable#run()
     */
    @Override
    public final void run() {
        needToCancel = false;
        fireProcessStarted();
        
        try {
            process();
        }
        catch (InterruptedException ie) {
            fireProcessInterrupted(ie);
        }
        
        fireProcessComplete();
    }

    
    /**
     * 
     */
    public final synchronized void cancel() {
        needToCancel = true;
    }
    
    
    /**
     * 
     * @return
     */
    public final boolean isCanceled() {
        return needToCancel;
    }

    
    /**
     * 
     * @param result
     */
    protected final void setResult(RESULT result) {
        this.result = result;
    }
    
    
    /**
     * 
     * @return
     */
    public final RESULT getResult() {
        return isProcessing() ? null : result;
    }
    
    /**
     * Process method of this instance of the <CODE>Process</CODE> object. This
     * method must be overwritten by the super class and is called from the
     * <I>run()</I> method defined by <CODE>Runnable</CODE>.
     * 
     * @throws InterruptedException
     */
    public abstract void process() throws InterruptedException;
    
    
    /**
     * 
     * @param pl
     */
    public final void addProcessListener(ProcessListener pl) {
        if (pl != null)
            listeners.add(pl);
    }
    
    
    /**
     * 
     * @param pl
     * @return
     */
    public final boolean removeProcessListener(ProcessListener pl) {
        if (pl != null)
            return listeners.remove(pl);
        return false;
    }
    
    
    /**
     * 
     */
    public final void removeAllProcessListener() {
        listeners.clear();
    }
    
    
    /**
     * 
     * @return
     */
    public synchronized final boolean isProcessing() {
        return processing;
    }
    
    
    /**
     * 
     */
    protected synchronized final void fireProcessStarted() {
        if (!processing) {
            processing = true;
            ProcessEvent pe =
                    new ProcessEvent(this, ProcessEvent.PROCEES_STARTED);

            notifyProcessListeners(pe);
        }
    }
    
    
    /**
     * 
     */
    protected synchronized final void fireProcessComplete() {
        if (processing) {
            processing = false;
            ProcessEvent pe =
                    new ProcessEvent(this, ProcessEvent.PROCESS_COMPLETED);

            notifyProcessListeners(pe);
        }
    }
    
    
    /**
     * 
     * @param ie
     */
    protected synchronized final void fireProcessInterrupted(InterruptedException ie) {
        if (processing) {
            processing = false;
            ProcessEvent pe =
                    new ProcessEvent(this, ProcessEvent.PROCESS_INTERRUPTED);
            
            pe.setException(ie);
            notifyProcessListeners(pe);
        }
    }
    
    
    /**
     * 
     * @param pe
     */
    private synchronized final void notifyProcessListeners(final ProcessEvent pe) {
        new Thread() {
            public void run() {
                for (ProcessListener pl : listeners) {
                    switch(pe.getEventType()) {
                    case ProcessEvent.PROCEES_STARTED:
                        pl.processStarted(pe);
                        break;
                    case ProcessEvent.PROCESS_COMPLETED:
                        pl.processCompleted(pe);
                        break;
                    case ProcessEvent.PROCESS_INTERRUPTED:
                        pl.processInterrupted(pe);
                        break;
                    }
                }
            }
        }.start();
    }
    
    
    /**
     * 
     * @author Chris Hartley
     *
     */
    public static class ProcessEvent extends EventObject {

        public static final int PROCEES_STARTED     = 0;
        public static final int PROCESS_COMPLETED   = 1;
        public static final int PROCESS_INTERRUPTED = 2;
        
        private static final long serialVersionUID = -4365351353635705754L;
        private final long occuredAt;
        private final int type;
        private Exception ex;

        
        /**
         * Constructor for the process event object.
         * 
         * @param p
         * @param eventType
         */
        public ProcessEvent(Process<?> p, int eventType) {
            super(p);
            type = eventType;
            occuredAt = System.currentTimeMillis();
        }
        
        
        /**
         * 
         * @return
         */
        public final long getWhen() {
            return occuredAt;
        }
        
        
        /**
         * 
         * @return
         */
        public final int getEventType() {
            return type;
        }
        
        
        /**
         * 
         * @param ex
         */
        protected final void setException(Exception ex) {
            this.ex = ex;
        }

        
        /**
         * 
         * @return
         */
        public final Exception getException() {
            return ex;
        }
        
    }
    
    
    /**
     * 
     * @author Chris Hartley
     *
     */
    public static interface ProcessListener extends EventListener {
        
        /**
         * Called once the specified <CODE>Process</CODE> has completed running
         * its <I>process()</I> method.
         * 
         * @param pe The <CODE>ProcessEvent</CODE> that has completed.
         */
        public void processCompleted(ProcessEvent pe);
        
        /**
         * Called once the specified <CODE>Process</CODE> has started running
         * its <I>process()</I> method.
         * 
         * @param pe The <CODE>ProcessEvent</CODE> that has started.
         */
        public void processStarted(ProcessEvent pe);
        
        /**
         * Called if the specified <CODE>Process</CODE> has been interrupted
         * while executing the <I>process()</I> method. To retrieve the
         * <CODE>InterruptionException</CODE>, use the <I>getException()</I>
         * method of the <CODE>ProcessEvent</CODE> object.
         * 
         * @param p  The <CODE>Process</CODE> that has been interrupted.
         */
        public void processInterrupted(ProcessEvent pe);
        
    }

}
