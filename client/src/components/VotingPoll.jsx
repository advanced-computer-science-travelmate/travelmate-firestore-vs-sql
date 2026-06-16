import { useEffect, useState } from "react";
import { destinationService } from "../services/destinationService";

function VotingPoll() {
  const [destinations, setDestinations] = useState([]);
  const [pollTitle, setPollTitle] = useState("");
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [poll, setPoll] = useState(() => {
    const savedPoll = localStorage.getItem("votingPoll");
    return savedPoll ? JSON.parse(savedPoll) : null;
  });

  useEffect(() => {
    async function loadDestinations() {
      try {
        const data = await destinationService.getEuropeanDestinations();
        setDestinations(data);
      } catch (error) {
        console.error("Failed to load destinations:", error);
      }
    }

    loadDestinations();
  }, []);

  useEffect(() => {
    if (poll) {
      localStorage.setItem("votingPoll", JSON.stringify(poll));
    }
  }, [poll]);

  function handleOptionToggle(destinationName) {
    if (selectedOptions.includes(destinationName)) {
      setSelectedOptions(
        selectedOptions.filter((option) => option !== destinationName)
      );
    } else {
      if (selectedOptions.length >= 5) return;
      setSelectedOptions([...selectedOptions, destinationName]);
    }
  }

  function handleCreatePoll(e) {
    e.preventDefault();

    const newPoll = {
      id: Date.now(),
      title: pollTitle,
      options: selectedOptions.map((destination) => ({
        destination,
        votes: 0,
      })),
    };

    setPoll(newPoll);
    setPollTitle("");
    setSelectedOptions([]);
  }

  function handleVote(destinationName) {
    const updatedPoll = {
      ...poll,
      options: poll.options.map((option) =>
        option.destination === destinationName
          ? { ...option, votes: option.votes + 1 }
          : option
      ),
    };

    setPoll(updatedPoll);
  }

  function handleDeletePoll() {
    localStorage.removeItem("votingPoll");
    setPoll(null);
  }

  const winner =
    poll?.options?.length > 0
      ? poll.options.reduce((highest, current) =>
          current.votes > highest.votes ? current : highest
        )
      : null;

  return (
    <section className="bg-white rounded-3xl shadow-sm p-8 mb-10">
      <div className="flex items-center justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">
            Group Destination Voting
          </h2>
          <p className="text-slate-600 mt-2">
            Let friends vote and choose the best destination together.
          </p>
        </div>

        {poll && (
          <button
            onClick={handleDeletePoll}
            className="border border-red-500 text-red-500 px-4 py-2 rounded-xl font-semibold hover:bg-red-50 transition"
          >
            Delete Poll
          </button>
        )}
      </div>

      {!poll ? (
        <form onSubmit={handleCreatePoll}>
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">
              Poll title
            </label>
            <input
              type="text"
              value={pollTitle}
              onChange={(e) => setPollTitle(e.target.value)}
              placeholder="Summer Europe Trip"
              required
              className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="mt-6">
            <p className="text-sm font-semibold text-slate-700 mb-3">
              Select 3 to 5 destinations
            </p>

            <div className="grid sm:grid-cols-2 md:grid-cols-4 gap-3">
              {destinations.map((destination) => (
                <button
                  type="button"
                  key={destination.id}
                  onClick={() => handleOptionToggle(destination.name)}
                  className={`border rounded-xl px-4 py-3 text-sm font-semibold transition ${
                    selectedOptions.includes(destination.name)
                      ? "bg-blue-600 text-white border-blue-600"
                      : "bg-white text-slate-700 border-slate-300 hover:bg-blue-50"
                  }`}
                >
                  {destination.name}
                </button>
              ))}
            </div>
          </div>

          <button
            type="submit"
            disabled={selectedOptions.length < 3}
            className="mt-6 bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition disabled:bg-slate-400"
          >
            Create Poll
          </button>
        </form>
      ) : (
        <>
          <div className="bg-blue-50 border border-blue-100 rounded-2xl p-5">
            <h3 className="text-xl font-bold text-slate-900">{poll.title}</h3>

            {winner && (
              <p className="text-blue-700 font-semibold mt-2">
                🏆 Current winner: {winner.destination} ({winner.votes} votes)
              </p>
            )}
          </div>

          <div className="grid md:grid-cols-3 gap-5 mt-6">
            {poll.options.map((option) => (
              <div
                key={option.destination}
                className="border border-slate-200 rounded-2xl p-5"
              >
                <h4 className="text-xl font-bold text-slate-900">
                  {option.destination}
                </h4>

                <p className="text-slate-600 mt-2">{option.votes} votes</p>

                <button
                  onClick={() => handleVote(option.destination)}
                  className="mt-5 w-full bg-blue-600 text-white py-2.5 rounded-xl font-semibold hover:bg-blue-700 transition"
                >
                  Vote
                </button>
              </div>
            ))}
          </div>
        </>
      )}
    </section>
  );
}

export default VotingPoll;