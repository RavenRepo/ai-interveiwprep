import { useEffect } from 'react';
import { useInterviewStore } from '../stores/useInterviewStore';
import { API_BASE_URL, TOKEN_KEY } from '../utils/constants';

export const useInterviewEvents = (interviewId: number) => {
  const { 
    setConnectionStatus, 
    updateQuestionVideoUrl, 
    setInterview 
  } = useInterviewStore();

  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) return;

    const url = `${API_BASE_URL}/api/interviews/${interviewId}/events?token=${token}`;
    const eventSource = new EventSource(url);

    setConnectionStatus('connecting');

    eventSource.onopen = () => {
      console.log('SSE Connected');
      setConnectionStatus('connected');
    };

    eventSource.onerror = (error) => {
      console.error('SSE Error:', error);
      setConnectionStatus('error');
      eventSource.close();
    };

    // Listen for AVATAR_READY events
    eventSource.addEventListener('AVATAR_READY', (event: MessageEvent) => {
      try {
        const data = JSON.parse(event.data);
        console.log('Avatar Ready:', data);
        // data structure: { questionId: 123, videoUrl: "..." }
        if (data.questionId && data.videoUrl) {
          updateQuestionVideoUrl(data.questionId, data.videoUrl);
        }
      } catch (err) {
        console.error('Failed to parse AVATAR_READY event', err);
      }
    });

    // Listen for INTERVIEW_READY events (all videos done)
    eventSource.addEventListener('INTERVIEW_READY', (event: MessageEvent) => {
      try {
        const data = JSON.parse(event.data);
        console.log('Interview Ready:', data);
        // data might be the full interview DTO or just status
        if (data.status) {
             useInterviewStore.setState(state => ({
                 interview: state.interview ? { ...state.interview, status: data.status } : null
             }));
        }
      } catch (err) {
         console.error('Failed to parse INTERVIEW_READY event', err);
      }
    });
    
    // Listen for INTERVIEW_ERROR events
    eventSource.addEventListener('INTERVIEW_ERROR', (event: MessageEvent) => {
        console.error('Interview Error Event:', event.data);
        setConnectionStatus('error');
    });

    return () => {
      console.log('Closing SSE connection');
      eventSource.close();
      setConnectionStatus('disconnected');
    };
  }, [interviewId, setConnectionStatus, updateQuestionVideoUrl, setInterview]);
};
